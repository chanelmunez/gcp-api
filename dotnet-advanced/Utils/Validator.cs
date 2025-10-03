using System.Text.RegularExpressions;

namespace AdvancedUserService.Utils
{
    public class Validator
    {
        private static readonly Regex EmailRegex = new Regex(
            @"^[^\s@]+@[^\s@]+\.[^\s@]+$",
            RegexOptions.Compiled
        );

        public bool IsValidEmail(string email)
        {
            return !string.IsNullOrEmpty(email) && EmailRegex.IsMatch(email);
        }

        public bool IsValidName(string name)
        {
            return !string.IsNullOrEmpty(name) && name.Length >= 2 && name.Length <= 100;
        }
    }
}
