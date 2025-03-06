import { ReactElement, useState } from 'react';
import './App.css';
import Input from './components/Input';
import Logo from './components/Logo';
import { Chat } from './utils/interfaces';
import ChatStream from './components/ChatStream';
import UserType from './utils/UserType';

function App(): ReactElement {

    const [chatStream, setChatStream] = useState<Chat[]>([{
        message: `
### Welcome to AskTheDev!
Please ask me anything, I am already caught up to date with the current webpage you are on!
        `,
        userType: UserType.AI,
        timestamp: Date.now()
    }]);
    const [prompt, setPrompt] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);
  
    return (
        <div className="items-center flex flex-col">
            <div className="overflow-scroll flex flex-col items-center h-[70vh] min-w-[50vw]">
                <Logo />
                <ChatStream
                    stream={[chatStream, setChatStream]}
                />
            </div>
            <div>
                <Input
                    prompt={[prompt, setPrompt]}
                    loading={[loading, setLoading]}
                    chatStream={[chatStream, setChatStream]}
                />
            </div>
        </div>
    );
}

export default App;
