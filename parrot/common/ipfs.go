package common

import (
	"context"
	"io/ioutil"
	"log"
	"os"
	"path/filepath"
	"sync"

	config "github.com/ipfs/go-ipfs-config"
	"github.com/ipfs/go-ipfs/core"
	"github.com/ipfs/go-ipfs/core/coreapi"
	"github.com/ipfs/go-ipfs/core/node/libp2p"
	"github.com/ipfs/go-ipfs/plugin/loader"
	"github.com/ipfs/go-ipfs/repo"
	"github.com/ipfs/go-ipfs/repo/fsrepo"
	icore "github.com/ipfs/interface-go-ipfs-core"
	"github.com/libp2p/go-libp2p-core/host"
	"github.com/libp2p/go-libp2p-core/peer"
	"github.com/multiformats/go-multiaddr"
)

func createSwarmKey(repoPath string, key string) error {
	keyPath := filepath.Join(repoPath, "swarm.key")

	if err := os.MkdirAll(repoPath, 0755); err != nil {
		return err
	}

	return ioutil.WriteFile(keyPath, []byte(key), 0600)
}

func deleteSwarmKey(repoPath string) error {
	keyPath := filepath.Join(repoPath, "swarm.key")

	if _, err := os.Stat(keyPath); err == nil {
		return os.Remove(keyPath)
	} else if os.IsNotExist(err) {
		return nil
	} else {
		return err
	}
}

func createRepo(repoPath string, swarmAddresses []string) (repo.Repo, error) {
	if !fsrepo.IsInitialized(repoPath) {
		conf, err := config.Init(ioutil.Discard, 2048)
		if err != nil {
			return nil, err
		}

		if err = fsrepo.Init(repoPath, conf); err != nil {
			return nil, err
		}
	}

	repo, err := fsrepo.Open(repoPath)
	if err != nil {
		return nil, err
	}

	conf, err := repo.Config()
	if err != nil {
		return nil, err
	}

	conf.Addresses.Swarm = swarmAddresses
	if err = repo.SetConfig(conf); err != nil {
		return nil, err
	}

	return repo, nil
}

func setupPlugins(repoPath string) error {
	plugins, err := loader.NewPluginLoader(repoPath)
	if err != nil {
		return err
	}

	if err = plugins.Initialize(); err != nil {
		return err
	}

	if err = plugins.Inject(); err != nil {
		return err
	}

	return nil
}

func CreateIpfsNode(ctx context.Context, repoPath string, swarmKey string, swarmAddresses []string) (icore.CoreAPI, error) {
	var err error
	if repoPath == "" {
		repoPath, err = config.PathRoot()
		if err != nil {
			return nil, err
		}
	}
	if repoPath, err = filepath.Abs(repoPath); err != nil {
		return nil, err
	}

	if swarmKey != "" {
		if err = createSwarmKey(repoPath, swarmKey); err != nil {
			return nil, err
		}
	} else {
		if err = deleteSwarmKey(repoPath); err != nil {
			return nil, err
		}
	}

	if err = setupPlugins(repoPath); err != nil {
		return nil, err
	}

	repo, err := createRepo(repoPath, swarmAddresses)
	if err != nil {
		return nil, err
	}

	options := &core.BuildCfg{
		Online:  true,
		Routing: libp2p.DHTOption,
		Repo:    repo,
	}

	node, err := core.NewNode(ctx, options)
	if err != nil {
		return nil, err
	}

	log.Printf("ipfs node %s started, addresses are", node.Identity)
	addrs, err := peer.AddrInfoToP2pAddrs(host.InfoFromHost(node.PeerHost))
	if err != nil {
		return nil, err
	}
	for _, addr := range addrs {
		log.Printf("\t%s", addr.String())
	}

	return coreapi.NewCoreAPI(node)
}

func ConnectIpfsPeers(ctx context.Context, ipfs icore.CoreAPI, peers []string) error {
	wg := new(sync.WaitGroup)
	peerInfos := make(map[peer.ID]*peer.AddrInfo, len(peers))

	for _, addrStr := range peers {
		addr, err := multiaddr.NewMultiaddr(addrStr)
		if err != nil {
			return err
		}

		addrInfo, err := peer.AddrInfoFromP2pAddr(addr)
		if err != nil {
			return err
		}

		peerInfo, ok := peerInfos[addrInfo.ID]
		if !ok {
			peerInfo = &peer.AddrInfo{ID: addrInfo.ID}
			peerInfos[peerInfo.ID] = peerInfo
		}
		peerInfo.Addrs = append(peerInfo.Addrs, addrInfo.Addrs...)
	}

	wg.Add(len(peerInfos))

	for _, peerInfo := range peerInfos {
		go func(peerInfo *peer.AddrInfo) {
			defer wg.Done()

			err := ipfs.Swarm().Connect(ctx, *peerInfo)
			if err != nil {
				log.Printf("failed to connect to %s: %s", peerInfo.ID, err)
			}
		}(peerInfo)
	}

	wg.Wait()

	return nil
}
