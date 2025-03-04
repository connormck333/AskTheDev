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
            <div className="overflow-scroll" style={{height: "70vh"}}>
                <Logo />
                <ChatStream
                    stream={[chatStream, setChatStream]}
                />
            </div>
            <div style={{height: "30vh"}}>
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
