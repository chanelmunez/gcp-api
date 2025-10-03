function isValidEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

function isValidName(name) {
  return name && name.length >= 2 && name.length <= 100;
}

module.exports = {
  isValidEmail,
  isValidName
};
