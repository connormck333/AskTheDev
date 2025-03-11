import { ReactElement, useEffect, useState } from 'react';
import './App.css';
import PromptScreen from './components/screens/PromptScreen';
import RegisterScreen from './components/screens/RegisterScreen';
import { Status } from './utils/interfaces';
import { getCurrentUser } from './methods/userManagement/getCurrentUser';

function App(): ReactElement {

    const [signedIn, setSignedIn] = useState<boolean | undefined>(undefined);
    const [signedInUser, setSignedInUser] = useState<any>();

    useEffect(() => {
        retrieveUserDetails();
    }, []);

    async function retrieveUserDetails(): Promise<void> {
        const response: Status = await getCurrentUser();
        if (!response.success) {
            setSignedIn(false);
        }

        setSignedInUser(response.data);
        setSignedIn(true);
    }

    if (signedIn === undefined) {
        return <div />
    }
  
    return (
        <div className="items-center flex flex-col main-container">
            { !signedIn ? <RegisterScreen /> : <PromptScreen /> }
        </div>
    );
}

export default App;
