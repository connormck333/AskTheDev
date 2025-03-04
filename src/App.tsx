import { ReactElement, useState } from 'react';
import './App.css';
import Input from './components/Input';
import Logo from './components/Logo';
import { Chat } from './utils/interfaces';
import ChatStream from './components/ChatStream';

function App(): ReactElement {

    const [chatStream, setChatStream] = useState<Chat[]>([]);
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
