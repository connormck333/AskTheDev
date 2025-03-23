import { JSX, ReactElement, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { User, LogOut, Settings, HelpCircle } from 'lucide-react';

type MenuItem = {
    name: string;
    icon: JSX.Element;
};

export default function FloatingAccountButton(): ReactElement {
    const [isMenuOpen, setIsMenuOpen] = useState<boolean>(false);

    const toggleMenu = (): void => setIsMenuOpen(prev => !prev);

    const menuItems: MenuItem[] = [
        { name: 'Profile', icon: <User size={18} /> },
        { name: 'Settings', icon: <Settings size={18} /> },
        { name: 'Help', icon: <HelpCircle size={18} /> },
        { name: 'Logout', icon: <LogOut size={18} /> }
    ];

    return (
        <div className="fixed top-4 right-4">
            {/* Account Button */}
            <button 
                onClick={toggleMenu} 
                className="p-2 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 focus:outline-none"
            >
                <User size={20} />
            </button>

            {/* Options Menu */}
            <AnimatePresence>
                {isMenuOpen && (
                    <motion.div
                        initial={{ opacity: 0, scale: 0.8 }}
                        animate={{ opacity: 1, scale: 1 }}
                        exit={{ opacity: 0, scale: 0.8 }}
                        transition={{ duration: 0.2 }}
                        className="absolute top-12 right-0 w-48 bg-white rounded-2xl shadow-xl p-2 space-y-2"
                    >
                        {menuItems.map((item) => (
                            <button
                                key={item.name}
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