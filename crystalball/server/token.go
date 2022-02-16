package main

import (
	"sync"

	"github.com/google/uuid"
)

type TokenStore struct {
	tokens sync.Map
}

func (s *TokenStore) Validate(token string) bool {
	_, ok := s.tokens.Load(token)
	return ok
}

func (s *TokenStore) Issue() string {
	token := uuid.NewString()
	s.tokens.Store(token, nil)
	return token
}

func (s *TokenStore) Revoke(token string) {
	s.tokens.Delete(token)
}
