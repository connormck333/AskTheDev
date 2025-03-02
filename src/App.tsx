import { ReactElement } from 'react';
import './App.css';
import Input from './components/Input';
import Logo from './components/Logo';

function App(): ReactElement {
  
    return (
        <div className="items-center flex flex-col">
            <Logo />
            <Input />
        </div>
    );
}

export default App;
