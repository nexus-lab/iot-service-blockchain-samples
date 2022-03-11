package main

import (
	"bytes"
	"context"
	"encoding/binary"
	"fmt"
	"log"

	pvrecorder "github.com/Picovoice/pvrecorder/sdk/go"
	"github.com/go-audio/wav"
	files "github.com/ipfs/go-ipfs-files"
	icore "github.com/ipfs/interface-go-ipfs-core"
	"github.com/traherom/memstream"
)

func recordAudio(ctx context.Context, ipfs icore.CoreAPI, wakeWordNotifier <-chan interface{}, voiceActivityNotifier <-chan bool, notifier chan<- string) error {
	const (
		BIT_RATE          = 16
		SAMPLE_RATE       = 16000      // sample rate consumed by rhino
		FRAME_LENGTH      = 512        // frame length consumed by rhino
		AUDIO_BUFFER_SIZE = 100 * 1024 // 100kb audio buffer
	)

	var err error
	var encoder *wav.Encoder
	var audio *memstream.MemoryStream

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

	active := false
	started := false

	start := func() {
		audio = memstream.NewCapacity(AUDIO_BUFFER_SIZE)
		encoder = wav.NewEncoder(audio, SAMPLE_RATE, BIT_RATE, 1, 1)

		log.Println("Recording started")
	}

	write := func() error {
		pcm, err := recorder.Read()
		if err != nil {
			return err
		}

		buf := new(bytes.Buffer)
		if err = binary.Write(buf, binary.LittleEndian, pcm); err != nil {
			return err
		}

		for bufIndex := range pcm {
			if err := encoder.WriteFrame(pcm[bufIndex]); err != nil {
				return err
			}
		}

		return nil
	}

	stop := func() error {
		if encoder != nil {
			if err = encoder.Close(); err != nil {
				return err
			}
			encoder = nil

			log.Println("Recording stopped")
		}

		if audio != nil {
			cid, err := ipfs.Unixfs().Add(ctx, files.NewBytesFile(audio.Bytes()))
			if err != nil {
				return err
			}
			audio = nil

			audioPath := fmt.Sprintf("/ipfs/%s", cid.Cid().String())
			log.Printf("Audio saved to %s", audioPath)

			notifier <- audioPath
		}

		return nil
	}

	for {
		select {
		case <-ctx.Done():
			return nil

		case active = <-voiceActivityNotifier:
			if started && !active {
				// stop recording
				if err := stop(); err != nil {
					return err
				}
				started = false
			}

		case <-wakeWordNotifier:
			if active {
				// start recording
				if !started {
					start()
					defer stop()
					started = true
				}
			}

		default:
			if started {
				if err := write(); err != nil {
					return err
				}
			}
		}
	}
}
