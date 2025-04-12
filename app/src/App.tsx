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
import SubscriptionType from './utils/SubscriptionType';

function App(): ReactElement {

    const [signedIn, setSignedIn] = useState<boolean | undefined>(undefined);
    const [signedInUser, setSignedInUser] = useState<User | undefined>(undefined);
    const [currentScreen, setCurrentScreen] = useState<ScreenType>(ScreenType.LOADING);

    useEffect(() => {
        retrieveUserDetails();
    }, []);

    async function retrieveUserDetails(): Promise<void> {
        const response: Status = await getCurrentUser();
        if (!response.success) {
            setSignedIn(false);
            setCurrentScreen(ScreenType.PROMPT);
            return;
        }

        const user: User = response.data;

        setSignedInUser(response.data);
        setSignedIn(true);

        if (user.subscriptionType === SubscriptionType.NONE) {
            setCurrentScreen(ScreenType.SUBSCRIPTION);
        } else {
            setCurrentScreen(ScreenType.PROMPT);
        }
    }

    if (signedIn === undefined) {
        return <LoadingScreen />
    }
  
    return (
        <div className="items-center justify-center flex flex-col main-container min-h-[500px]">
            <RenderScreen
                signedIn={signedIn}
                signedInUser={signedInUser}
                currentScreen={currentScreen}
                setSignedIn={setSignedIn}
                setSignedInUser={setSignedInUser}
                setCurrentScreen={setCurrentScreen}
            />
        </div>
    );
}

interface RenderScreenProps {
    signedIn: boolean;
    signedInUser: User | undefined;
    currentScreen: ScreenType | undefined;
    setSignedIn: Function;
    setSignedInUser: Function;
    setCurrentScreen: Function;
}

function RenderScreen(props: RenderScreenProps) {

    const { setSignedIn, setSignedInUser, setCurrentScreen } = props;

    if (props.currentScreen === ScreenType.LOADING) {
        return <LoadingScreen />;
    } else if (props.currentScreen === ScreenType.LOGIN) {
        return <LoginScreen setSignedIn={setSignedIn} setUser={setSignedInUser} setCurrentScreen={setCurrentScreen} />;
    } else if (props.currentScreen === ScreenType.REGISTER) {
        return <RegisterScreen setSignedIn={setSignedIn} setUser={setSignedInUser} setCurrentScreen={setCurrentScreen} />;
    } else if (props.signedInUser !== undefined && props.currentScreen === ScreenType.SUBSCRIPTION) {
        return <SubscriptionScreen setSignedIn={setSignedIn} user={props.signedInUser} setUser={setSignedInUser} setScreen={setCurrentScreen} />
    }

    return <PromptScreen setSignedIn={setSignedIn} user={props.signedInUser} setScreen={setCurrentScreen} />
}

export default App;
