import { JSX, ReactElement, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { User, LogOut, Settings, HelpCircle } from 'lucide-react';
import { Status } from '../utils/interfaces';
import { logout } from '../methods/userManagement/logout';

type MenuItem = {
    name: string;
    id: string;
    icon: JSX.Element;
};

interface FloatingButtonProps {
    setSignedIn: Function;
};

export default function FloatingAccountButton(props: FloatingButtonProps): ReactElement {

    const { setSignedIn } = props;
    const [isMenuOpen, setIsMenuOpen] = useState<boolean>(false);

    const toggleMenu = (): void => setIsMenuOpen(prev => !prev);

    const menuItems: MenuItem[] = [
        { name: 'Profile', id: 'profile', icon: <User size={18} /> },
        { name: 'Settings', id: 'settings', icon: <Settings size={18} /> },
        { name: 'Help', id: 'help', icon: <HelpCircle size={18} /> },
        { name: 'Logout', id: 'logout', icon: <LogOut size={18} /> }
    ];

    function handleOptionClick(id: string) {
        switch (id) {
            case "logout": 
                logoutUser();
                break;
        }
    }

    async function logoutUser(): Promise<void> {
        const response: Status = await logout();
        if (!response.success) {
            alert("There was an error logging you out. Please try again later.");
            return;
        }

        setSignedIn(false);
    }

    return (
        <div className="fixed top-4 right-4">
            <button 
                onClick={toggleMenu} 
                className="p-2 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 focus:outline-none"
            >
                <User size={20} />
            </button>

            <AnimatePresence>
                {isMenuOpen && (
                    <motion.div
                        initial={{ opacity: 0, scale: 0.8 }}
                        animate={{ opacity: 1, scale: 1 }}
                        exit={{ opacity: 0, scale: 0.8 }}
                        transition={{ duration: 0.2 }}
                        className="absolute top-12 right-0 w-48 bg-white rounded-2xl shadow-xl p-2 space-y-2"
                    >
                        { menuItems.map((item) => (
                            <button
                                key={item.name}
                                onClick={() => handleOptionClick(item.id)}
                                className="flex items-center w-full p-2 text-sm text-gray-700 hover:bg-gray-100 rounded-lg"
                            >
                                { item.icon }
                                <span className="ml-2">{ item.name }</span>
                            </button>
                        ))}
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
};