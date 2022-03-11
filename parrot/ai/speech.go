package main

import (
	"context"
	"errors"
	"fmt"
	"io"
	"os"
	"path/filepath"
	"strings"

	rhino "github.com/Picovoice/rhino/binding/go/v2"
	"github.com/go-audio/audio"
	"github.com/go-audio/wav"
	"github.com/nexus-lab/iot-service-blockchain-samples/parrot/common"
)

func runSpeechToIntent(ctx context.Context, assetsDir, accessKey string, audioFile io.ReadSeeker) (*rhino.RhinoInference, error) {
	const (
		BIT_RATE      = 16
		NUMS_CHANNELS = 1
	)

	osName, _, err := common.GetOS()
	if err != nil {
		return nil, err
	}

	version := strings.ReplaceAll(rhino.Version, ".", "_")
	contextName := fmt.Sprintf("smart-lighting_en_%s_v%s.rhn", osName, version)
	contextPath, err := filepath.Abs(filepath.Join(assetsDir, contextName))
	if err != nil {
		return nil, errors.New("failed to get context file path")
	}
	if _, err = os.Stat(contextPath); errors.Is(err, os.ErrNotExist) {
		return nil, fmt.Errorf("context file %s does not exist", contextPath)
	}

	engine := &rhino.Rhino{
		AccessKey:       accessKey,
		RequireEndpoint: false,
		ContextPath:     contextPath,
		Sensitivity:     0.5,
	}
	if err = engine.Init(); err != nil {
		return nil, err
	}
	defer engine.Delete()

	wavFile := wav.NewDecoder(audioFile)
	if !wavFile.IsValidFile() || wavFile.BitDepth != BIT_RATE || wavFile.SampleRate != uint32(rhino.SampleRate) || wavFile.NumChans != NUMS_CHANNELS {
		return nil, fmt.Errorf("input audio must be %d-channel, %d-bit, %dHz linearly encoded PCM", NUMS_CHANNELS, BIT_RATE, rhino.SampleRate)
	}

	buf := &audio.IntBuffer{
		Format: &audio.Format{
			NumChannels: NUMS_CHANNELS,
			SampleRate:  rhino.SampleRate,
		},
		Data:           make([]int, rhino.FrameLength),
		SourceBitDepth: BIT_RATE,
	}

	// append 32 empty frames as endpoint
	suffix := 32
	completed := false
	pcm := make([]int16, rhino.FrameLength)

waitLoop:
	for !completed && suffix > -1 {
		select {
		case <-ctx.Done():
			break waitLoop

		default:
			read, err := wavFile.PCMBuffer(buf)
			if err != nil {
				return nil, err
			}

			if read > 0 {
				for i := range buf.Data {
					pcm[i] = int16(buf.Data[i])
				}
			} else {
				suffix--
				for i := range pcm {
					pcm[i] = 0
				}
			}

			if completed, err = engine.Process(pcm); err != nil {
				return nil, err
			}
		}
	}

	if !completed {
		return nil, fmt.Errorf("reached the end of the file before Rhino returned an inference")
	}

	inference, err := engine.GetInference()
	if err != nil {
		return nil, err
	}

	if inference.IsUnderstood {
		return &inference, nil
	} else {
		return nil, errors.New("unknown voice command")
	}
}
