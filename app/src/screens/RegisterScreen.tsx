import { ReactElement, useState } from "react";
import FormInput from "../components/FormInput";
import { isValidEmail, isValidPassword } from "../utils/inputValidation";
import { Status } from "../utils/interfaces";
import { createAccount } from "../methods/userManagement/createAccount";
import Loading from "../components/Loading";
import ScreenType from "../utils/ScreenType";

interface RegisterScreenProps {
    setUser: Function;
    setSignedIn: Function;
    setCurrentScreen: Function;
}

export default function RegisterScreen(props: RegisterScreenProps): ReactElement {

    const { setUser, setSignedIn, setCurrentScreen } = props;
    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [confirmPassword, setConfirmPassword] = useState<string>("");
    const [termsAccepted, setTermsAccepted] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(false);

    async function createUserAccount(): Promise<void> {
        if (!termsAccepted) {
            alert("You must accept the Terms & Conditions.");
            return;
        } else if (!isValidEmail(email)) {
            alert("Invalid email.");
            return;
        } else if (!isValidPassword(password)) {
            alert("Your password is not strong enough. It must be 8 characters or longer & contains a special character.");
            return;
        } else if (password !== confirmPassword) {
            alert("Your passwords do not match!");
            return;
        }

        setLoading(true);

        const response: Status = await createAccount(email, password);

        setLoading(false);

        if (!response.success) {
            alert(
                response.errorMessage
                ? response.errorMessage
                : "There was an error creating your account. Please try again later."
            );
            return;
        }

        setUser(response.data);
        setSignedIn(true);
        setCurrentScreen(undefined);
    }

    function login(): void {
        setCurrentScreen(ScreenType.LOGIN);
    }

    return (
        <section>
            <div className="flex flex-col items-center justify-center px-6 py-8 mx-auto md:h-screen lg:py-0">
                <div className="flex items-center mb-6">
                    <a target="_blank" href="https://askthedev.io">
                        <img className="w-auto h-10 mr-2 dark:hidden" src="/logo.png" alt="logo"/>
                        <img className="hidden w-auto h-10 mr-2 dark:block" src="/logo_dark.png" alt="logo"/>
                    </a>
                </div>
                <div className="w-full md:mt-0 sm:max-w-md xl:p-0">
                    <div className="space-y-4 md:space-y-6 sm:p-8">
                        <h1 className="font-bold leading-tight tracking-tight text-gray-900 dark:text-white">
                            Create an account
                        </h1>
                        <form className="space-y-4 md:space-y-6" action="#">
                            <FormInput
                                value={[email, setEmail]}
                                label="Your email"
                                placeholder="name@company.com"
                                type="email"
                            />
                            <FormInput
                                value={[password, setPassword]}
                                label="Password"
                                placeholder="••••••••"
                                type="password"
                            />
                            <FormInput
                                value={[confirmPassword, setConfirmPassword]}
                                label="Confirm password"
                                placeholder="••••••••"
                                type="password"
                            />
                            <div className="flex items-start">
                                <div className="flex items-center h-5">
                                    <input
                                        checked={termsAccepted}
                                        onChange={() => setTermsAccepted(!termsAccepted)}
                                        aria-describedby="terms"
                                        type="checkbox"
                                        className="w-4 h-4 border border-gray-300 rounded bg-gray-50 dark:border-gray-200"
                                    />
                                </div>
                                <div className="ml-3 text-sm">
                                    <label className="font-light text-gray-500 dark:text-gray-300">I accept the <a className="font-medium text-primary-600 hover:underline dark:text-primary-500" target="_blank" href="https://askthedev.io/terms">Terms and Conditions</a></label>
                                </div>
                            </div>
                            <button
                                type="button"
                                onClick={createUserAccount}
                                className="w-full text-white bg-blue-600 hover:bg-blue-700 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
                            >
                                <Loading
                                    loading={loading}
                                >
                                    Create an account
                                </Loading>
                            </button>
                            <p className="text-sm font-light text-gray-500 dark:text-gray-400">
                                Already have an account? <a onClick={login} className="font-medium text-primary-600 hover:underline dark:text-primary-500">Login here</a>
                            </p>
                        </form>
                    </div>
                </div>
            </div>
        </section>
    );
}