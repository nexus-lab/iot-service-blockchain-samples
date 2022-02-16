package main

import (
	"io"
	"sync"

	"github.com/nareix/joy4/av/avutil"
	"github.com/nareix/joy4/av/pubsub"
	"github.com/nareix/joy4/format"
	"github.com/nareix/joy4/format/rtmp"
	"github.com/sirupsen/logrus"
)

func init() {
	format.RegisterAll()
}

type channel struct {
	lock  *sync.RWMutex
	queue *pubsub.Queue
}

func createPublishHandler(ch *channel, streamKey string) func(*rtmp.Conn) {
	return func(conn *rtmp.Conn) {
		defer func() {
			if ch.queue != nil {
				ch.queue.Close()
				ch.lock.Lock()
				ch.queue = nil
				ch.lock.Unlock()

				logrus.Info("publisher disconnected: ", conn.NetConn().RemoteAddr())
				conn.Close()
			}
		}()

		if conn.URL.Query().Get("key") != streamKey {
			logrus.Warn("invalid streaming key")
			return
		}

		ch.lock.RLock()
		queue := ch.queue
		ch.lock.RUnlock()

		// replace publisher with the new one
		if queue != nil {
			if err := queue.Close(); err != nil {
				logrus.Error("failed to close previous publisher: ", err)
				return
			}
		}

		ch.lock.Lock()
		queue = pubsub.NewQueue()
		ch.queue = queue
		ch.lock.Unlock()

		streams, err := conn.Streams()
		if err != nil {
			logrus.Error("failed to get streams from publisher: ", err)
			return
		}

		logrus.Info("publisher connected: ", conn.NetConn().RemoteAddr())
		queue.WriteHeader(streams)

		if err = avutil.CopyPackets(queue, conn); err != nil && err != io.EOF {
			logrus.Error("failed to stream from publisher: ", err)
		}
	}
}

func createPlayHandler(ch *channel, tokenStore *TokenStore) func(*rtmp.Conn) {
	return func(conn *rtmp.Conn) {
		defer func() {
			logrus.Info("consumer disconnected: ", conn.NetConn().RemoteAddr())
			conn.Close()
		}()

		ch.lock.RLock()
		queue := ch.queue
		ch.lock.RUnlock()

		if queue == nil {
			logrus.Warn("cannot play because there is no publisher")
			return
		}

		token := conn.URL.Query().Get("token")
		if !tokenStore.Validate(token) {
			logrus.Warn("invalid streaming token")
			return
		}

		// immediately revoke used one-time token
		tokenStore.Revoke(token)

		logrus.Info("consumer connected: ", conn.NetConn().RemoteAddr())
		cursor := queue.Latest()
		if err := avutil.CopyFile(conn, cursor); err != nil && err != io.EOF {
			logrus.Error("failed to stream to consumer: ", err)
		}
	}
}

func NewRtmpServer(addr string, streamKey string, tokenStore *TokenStore) *rtmp.Server {
	ch := &channel{lock: &sync.RWMutex{}}
	return &rtmp.Server{
		Addr:          addr,
		HandlePublish: createPublishHandler(ch, streamKey),
		HandlePlay:    createPlayHandler(ch, tokenStore),
	}
}
