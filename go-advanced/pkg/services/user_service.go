package services

import (
	"errors"
	"go-advanced/pkg/models"
	"go-advanced/pkg/utils"
)

type UserService struct {
	users  map[int]*models.User
	nextID int
}

func NewUserService() *UserService {
	return &UserService{
		users:  make(map[int]*models.User),
		nextID: 1,
	}
}

func (s *UserService) CreateUser(name, email string) (*models.User, error) {
	if !utils.ValidatorInstance.IsValidName(name) {
		utils.Log.Error("Invalid name provided", map[string]interface{}{"name": name})
		return nil, errors.New("invalid name: must be between 2 and 100 characters")
	}

	if !utils.ValidatorInstance.IsValidEmail(email) {
		utils.Log.Error("Invalid email provided", map[string]interface{}{"email": email})
		return nil, errors.New("invalid email format")
	}

	user := models.NewUser(s.nextID, name, email)
	s.users[s.nextID] = user
	s.nextID++

	utils.Log.Info("User created successfully", map[string]interface{}{"user_id": user.ID})
	return user, nil
}

func (s *UserService) GetUser(id int) *models.User {
	return s.users[id]
}

func (s *UserService) GetAllUsers() []*models.User {
	users := make([]*models.User, 0, len(s.users))
	for _, user := range s.users {
		users = append(users, user)
	}
	return users
}
