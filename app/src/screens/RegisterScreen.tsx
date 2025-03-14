import { ReactElement, useState } from "react";
import FormInput from "../components/FormInput";
import { isValidEmail, isValidPassword } from "../utils/inputValidation";
import { Status } from "../utils/interfaces";
import { createAccount } from "../methods/userManagement/createAccount";

export default function RegisterScreen(): ReactElement {

    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [confirmPassword, setConfirmPassword] = useState<string>("");

    async function createUserAccount(): Promise<void> {
        if (!isValidEmail(email)) {
            alert("Invalid email.");
            return;
        } else if (!isValidPassword(password)) {
            alert("Your password is not strong enough. It must be 8 characters or longer & contains a special character.");
            return;
        } else if (password !== confirmPassword) {
            alert("Your passwords do not match!");
            return;
        }

        const response: Status = await createAccount(email, password);

        if (!response.success) {
            alert("There was an error creating your account. Please try again later.");
            return;
        }
    }

    return (
        <section className="bg-gray-50 dark:bg-gray-900">
            <div className="flex flex-col items-center justify-center px-6 py-8 mx-auto md:h-screen lg:py-0">
                <a href="#" className="flex items-center mb-6 text-2xl font-semibold text-gray-900 dark:text-white">
                    <img className="w-auto h-10 mr-2" src="/logo.png" alt="logo"/>
                </a>
                <div className="w-full bg-white rounded-lg shadow dark:border md:mt-0 sm:max-w-md xl:p-0 dark:bg-gray-800 dark:border-gray-700">
                    <div className="p-6 space-y-4 md:space-y-6 sm:p-8">
                        <h1 className="font-bold leading-tight tracking-tight text-gray-900 dark:text-white">
                            Create an account
                        </h1>
                        <form className="space-y-4 md:space-y-6" action="#">
                            <FormInput
                                value={[email, setEmail]}
                                label="Your email"
                                placeholder="name@company.com"
                            />
                            <FormInput
                                value={[password, setPassword]}
                                label="Password"
                                placeholder="••••••••"
                            />
                            <FormInput
                                value={[confirmPassword, setConfirmPassword]}
                                label="Confirm password"
                                placeholder="••••••••"
                            />
                            <div className="flex items-start">
                                <div className="flex items-center h-5">
                                    <input aria-describedby="terms" type="checkbox" className="w-4 h-4 border border-gray-300 rounded bg-gray-50 focus:ring-3 focus:ring-primary-300 dark:bg-gray-700 dark:border-gray-600 dark:focus:ring-primary-600 dark:ring-offset-gray-800" />
                                </div>
                                <div className="ml-3 text-sm">
                                    <label className="font-light text-gray-500 dark:text-gray-300">I accept the <a className="font-medium text-primary-600 hover:underline dark:text-primary-500" href="#">Terms and Conditions</a></label>
                                </div>
                            </div>
                            <button
                                type="button"
                                onClick={createUserAccount}
                                className="w-full text-white bg-blue-600 hover:bg-blue-700 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
                            >Create an account</button>
                            <p className="text-sm font-light text-gray-500 dark:text-gray-400">
                                Already have an account? <a href="#" className="font-medium text-primary-600 hover:underline dark:text-primary-500">Login here</a>
                            </p>
                        </form>
                    </div>
                </div>
            </div>
            </section>
    );
}