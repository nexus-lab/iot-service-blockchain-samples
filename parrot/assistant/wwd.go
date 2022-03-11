package main

import (
	"context"
	"errors"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"strings"

	porcupine "github.com/Picovoice/porcupine/binding/go/v2"
	pvrecorder "github.com/Picovoice/pvrecorder/sdk/go"
	"github.com/nexus-lab/iot-service-blockchain-samples/parrot/common"
)

func detectWakeWord(ctx context.Context, picoAssetsDir string, accessKey string, notifier chan<- interface{}) error {
	osName, _, err := common.GetOS()
	if err != nil {
		return err
	}

	version := strings.ReplaceAll(porcupine.Version, ".", "_")
	keywordName := fmt.Sprintf("parrot_en_%s_v%s.ppn", osName, version)
	keywordPath, err := filepath.Abs(filepath.Join(picoAssetsDir, keywordName))
	if err != nil {
		return errors.New("failed to get keyword file path")
	}
	if _, err = os.Stat(keywordPath); errors.Is(err, os.ErrNotExist) {
		return fmt.Errorf("keyword file %s does not exist", keywordPath)
	}

	p := &porcupine.Porcupine{
		AccessKey:     accessKey,
		KeywordPaths:  []string{keywordPath},
		Sensitivities: []float32{0.5},
	}
	if err = p.Init(); err != nil {
		return err
	}
	defer p.Delete()

	recorder := pvrecorder.PvRecorder{
		DeviceIndex:    -1,
		FrameLength:    porcupine.FrameLength,
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

	log.Println("Wake word detection started")

	for {
		select {
		case <-ctx.Done():
			return nil

		default:
			pcm, err := recorder.Read()
			if err != nil {
				return nil
			}

			keywordIndex, err := p.Process(pcm)
			if err != nil {
				return nil
			}

			if keywordIndex > -1 {
				notifier <- nil
			}
		}
	}
}
