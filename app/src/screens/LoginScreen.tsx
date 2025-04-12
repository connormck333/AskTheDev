import { ReactElement, useState } from "react";
import FormInput from "../components/FormInput";
import Loading from "../components/Loading";
import ScreenType from "../utils/ScreenType";
import { Status } from "../utils/interfaces";
import { login } from "../methods/userManagement/login";

interface LoginScreenProps {
    setUser: Function;
    setSignedIn: Function;
    setCurrentScreen: Function;
}

export default function LoginScreen(props: LoginScreenProps): ReactElement {

    const { setCurrentScreen, setUser, setSignedIn } = props;
    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);

    async function loginUser(): Promise<void> {
        if (email.length < 4 || password.length < 4) {
            alert("Please enter your email and password.");
            return;
        }

        setLoading(true);
        
        const response: Status = await login(email, password);

        setLoading(false);

        if (!response.success) {
            alert(
                response.errorMessage
                ? response.errorMessage
                : "There was an error logging you in. Make sure you have entered the correct email and password."
            );
            return;
        }

        setUser({...response.data});
        setSignedIn(true);
        setCurrentScreen(ScreenType.PROMPT);
    }

    function register(): void {
        setCurrentScreen(ScreenType.REGISTER);
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
                            Welcome Back!
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
                            <button
                                type="button"
                                onClick={loginUser}
                                className="w-full text-white bg-blue-600 hover:bg-blue-700 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
                            >
                                <Loading
                                    loading={loading}
                                >
                                    Login
                                </Loading>
                            </button>
                            <p className="text-sm font-light text-gray-500 dark:text-gray-400">
                                Don't have an account? <a onClick={register} className="font-medium text-primary-600 hover:underline dark:text-primary-500">Sign up here</a>
                            </p>
                        </form>
                    </div>
                </div>
            </div>
        </section>
    );
}