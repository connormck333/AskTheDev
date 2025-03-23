import { ReactElement, useEffect, useState } from 'react';
import './App.css';
import PromptScreen from './screens/PromptScreen';
import RegisterScreen from './screens/RegisterScreen';
import { Status, User } from './utils/interfaces';
import { getCurrentUser } from './methods/userManagement/getCurrentUser';
import SubscriptionScreen from './screens/SubscriptionScreen';

function App(): ReactElement {

    const [signedIn, setSignedIn] = useState<boolean | undefined>(false);
    const [signedInUser, setSignedInUser] = useState<User | undefined>(undefined);

    useEffect(() => {
        retrieveUserDetails();
    }, []);

    async function retrieveUserDetails(): Promise<void> {
        const response: Status = await getCurrentUser();
        if (!response.success) {
            setSignedIn(false);
            return;
        }

        if (signedIn || signedInUser) {}

        setSignedInUser(response.data);
        setSignedIn(true);
    }

    if (signedIn === undefined) {
        return <div />
    }
  
    return (
        <div className="items-center flex flex-col main-container">
            { 
                !signedIn
                ? <RegisterScreen setSignedIn={setSignedIn} setUser={setSignedInUser} />
                : (signedInUser?.activeSubscription
                    ? <PromptScreen setSignedIn={setSignedIn} user={signedInUser} />
                    : <SubscriptionScreen user={signedInUser as User} />
                )
            }
        </div>
    );
}

export default App;
