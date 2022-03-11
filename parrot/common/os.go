package common

import (
	"fmt"
	"os/exec"
	"runtime"
	"strings"
)

func GetOS() (string, string, error) {
	switch os := runtime.GOOS; os {
	case "darwin":
		return "mac", getMacArch(), nil
	case "linux":
		osName, cpu, err := getLinuxDetails()
		return osName, cpu, err
	case "windows":
		return "windows", "amd64", nil
	default:
		return "", "", fmt.Errorf("Unsupported OS")
	}
}

func getMacArch() string {
	if runtime.GOARCH == "arm64" {
		return "arm64"
	} else {
		return "x86_64"
	}
}

func getLinuxDetails() (string, string, error) {
	var archInfo = ""

	if runtime.GOARCH == "amd64" {
		return "linux", "x86_64", nil
	} else if runtime.GOARCH == "arm64" {
		archInfo = "-aarch64"
	}

	cmd := exec.Command("cat", "/proc/cpuinfo")
	cpuInfo, err := cmd.Output()

	if err != nil {
		return "", "", err
	}

	var cpuPart = ""
	for _, line := range strings.Split(string(cpuInfo), "\n") {
		if strings.Contains(line, "CPU part") {
			split := strings.Split(line, " ")
			cpuPart = strings.ToLower(split[len(split)-1])
			break
		}
	}

	switch cpuPart {
	case "0xb76":
		return "raspberry-pi", "arm11" + archInfo, nil
	case "0xc07":
		return "raspberry-pi", "cortex-a7" + archInfo, nil
	case "0xd03":
		return "raspberry-pi", "cortex-a53" + archInfo, nil
	case "0xd07":
		return "jetson", "cortex-a57" + archInfo, nil
	case "0xd08":
		return "raspberry-pi", "cortex-a72" + archInfo, nil
	case "0xc08":
		return "beaglebone", "", nil
	default:
		return "", "", nil
	}
}
