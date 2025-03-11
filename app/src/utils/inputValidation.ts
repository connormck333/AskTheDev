const specialCharacters: Array<string> = ['!', '@', '£', '#', '$', '%', '^', '&', '*', '-'];

function containsSpecialCharacter(str: string): boolean {
    for (let char of specialCharacters) {
        if (str.includes(char)) {
            return true;
        }
    }

    return false;
}

function isValidPassword(password: string): boolean {
    return containsSpecialCharacter(password) && password.length >= 8;
}

function isValidEmail(email: string): boolean {
    return email.includes("@") && email.includes(".") && email.length >= 5;
}

export {
    isValidPassword,
    isValidEmail
}