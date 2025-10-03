package utils

import "regexp"

type Validator struct{}

func (v *Validator) IsValidEmail(email string) bool {
	emailRegex := regexp.MustCompile(`^[^\s@]+@[^\s@]+\.[^\s@]+$`)
	return emailRegex.MatchString(email)
}

func (v *Validator) IsValidName(name string) bool {
	return len(name) >= 2 && len(name) <= 100
}

var ValidatorInstance = &Validator{}
