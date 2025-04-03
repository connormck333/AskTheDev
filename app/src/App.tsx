import { ReactElement, useEffect, useState } from 'react';
import './App.css';
import PromptScreen from './screens/PromptScreen';
import RegisterScreen from './screens/RegisterScreen';
import { Status, User } from './utils/interfaces';
import { getCurrentUser } from './methods/userManagement/getCurrentUser';
import SubscriptionScreen from './screens/SubscriptionScreen';
import LoginScreen from './screens/LoginScreen';
import ScreenType from './utils/ScreenType';
import LoadingScreen from './screens/LoadingScreen';

function App(): ReactElement {

    const [signedIn, setSignedIn] = useState<boolean | undefined>(false);
    const [signedInUser, setSignedInUser] = useState<User | undefined>(undefined);
    const [currentScreen, setCurrentScreen] = useState<ScreenType>(ScreenType.LOGIN);

    useEffect(() => {
        retrieveUserDetails();
    }, []);

    async function retrieveUserDetails(): Promise<void> {
        const response: Status = await getCurrentUser();
        if (!response.success) {
            setSignedIn(false);
            return;
        }

        setSignedInUser(response.data);
        setSignedIn(true);
    }

    if (signedIn === undefined) {
        return <LoadingScreen />
    }
  
    return (
        <div className="items-center justify-center flex flex-col main-container min-h-[500px]">
            { 
                !signedIn
                ? (currentScreen === ScreenType.LOGIN
                    ? <LoginScreen setSignedIn={setSignedIn} setUser={setSignedInUser} setCurrentScreen={setCurrentScreen} />
                    : <RegisterScreen setSignedIn={setSignedIn} setUser={setSignedInUser} setCurrentScreen={setCurrentScreen} />
                )
                : (signedInUser?.activeSubscription
                    ? <PromptScreen setSignedIn={setSignedIn} user={signedInUser} />
                    : <SubscriptionScreen setSignedIn={setSignedIn} user={signedInUser as User} />
                )
            }
        </div>
    );
}

export default App;
