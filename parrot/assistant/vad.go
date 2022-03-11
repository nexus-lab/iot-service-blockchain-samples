package main

import (
	"bytes"
	"context"
	"encoding/binary"
	"log"

	pvrecorder "github.com/Picovoice/pvrecorder/sdk/go"
	vad "github.com/baabaaox/go-webrtcvad"
)

func detectVoiceActivity(ctx context.Context, notifier chan<- bool) error {
	const SAMPLE_RATE = 16000
	const FRAME_LENGTH = 20 * SAMPLE_RATE / 1000

	var err error

	vadInst := vad.Create()
	defer vad.Free(vadInst)

	if err = vad.Init(vadInst); err != nil {
		return err
	}
	if err = vad.SetMode(vadInst, 0); err != nil {
		return err
	}

	recorder := pvrecorder.PvRecorder{
		DeviceIndex:    -1,
		FrameLength:    FRAME_LENGTH,
		BufferSizeMSec: 1000,
		LogOverflow:    0,
	}
	if err := recorder.Init(); err != nil {
		return err
	}
	defer recorder.Delete()

	if err := recorder.Start(); err != nil {
		return err
	}

	prevActive := false

	log.Println("Voice activity detection started")

	for {
		select {
		case <-ctx.Done():
			return nil

		default:
			pcm, err := recorder.Read()
			if err != nil {
				return err
			}

			buf := new(bytes.Buffer)
			if err = binary.Write(buf, binary.LittleEndian, pcm); err != nil {
				return err
			}

			active, err := vad.Process(vadInst, SAMPLE_RATE, buf.Bytes(), FRAME_LENGTH)
			if err != nil {
				return err
			}

			if active != prevActive {
				notifier <- active
				prevActive = active
			}
		}
	}
}
