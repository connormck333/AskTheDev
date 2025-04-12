import { JSX, ReactElement, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import * as Icon from 'lucide-react';
import { Status, User } from '../utils/interfaces';
import { logout } from '../methods/userManagement/logout';
import { createManageSubscriptionSession } from '../methods/payments/createManageSubscriptionSession';
import Spinner from './Spinner';
import SubscriptionType from '../utils/SubscriptionType';
import ScreenType from '../utils/ScreenType';

type MenuItem = {
    name: string;
    id: string;
    icon: JSX.Element;
};

interface FloatingButtonProps {
    user: User;
    setSignedIn: Function;
    setScreen: Function;
};

export default function FloatingAccountButton(props: FloatingButtonProps): ReactElement {

    const { user, setSignedIn, setScreen } = props;
    const [isMenuOpen, setIsMenuOpen] = useState<boolean>(false);
    const [loading, setLoading] = useState<string | undefined>(undefined);

    const toggleMenu = (): void => setIsMenuOpen(prev => !prev);

    const menuItems: MenuItem[] = [
        { name: 'Subscription', id: 'subscription', icon: <Icon.Settings2 size={18} /> },
        { name: 'Help', id: 'help', icon: <Icon.HelpCircle size={18} /> },
        { name: 'Privacy', id: 'privacy', icon: <Icon.ScrollText size={18} /> },
        { name: 'Logout', id: 'logout', icon: <Icon.LogOut size={18} /> }
    ];

    function handleOptionClick(id: string) {
        switch (id) {
            case "logout": 
                logoutUser();
                break;
            case "subscription":
                createManageSubscriptionUrl();
                break;
            case "help":
                goToFAQ();
                break;
            case "privacy":
                goToPrivacy();
                break;
        }
    }

    async function logoutUser(): Promise<void> {
        setLoading("logout");

        const response: Status = await logout();

        setLoading(undefined);

        if (!response.success) {
            alert("There was an error logging you out. Please try again later.");
            return;
        }

        setSignedIn(false);
        setScreen(ScreenType.LOGIN);
    }

    async function createManageSubscriptionUrl(): Promise<void> {
        if (user.subscriptionType === SubscriptionType.FREE) {
            setScreen(ScreenType.SUBSCRIPTION);
            return;
        }

        setLoading("subscription")

        const response: Status = await createManageSubscriptionSession(user.userId);

        setLoading(undefined);

        if (!response.success) {
            alert("There was an error creating subscription management url. If this issue continues, please contact us.");
            return;
        }

        openManageSubscriptionPage(response.data.url);
    }

    function openManageSubscriptionPage(url: string): void {
        window.open(url);
    }

    function goToFAQ(): void {
        window.open("https://askthedev.io/#faq");
    }

    function goToPrivacy(): void {
        window.open("https://askthedev.io/privacy");
    }

    return (
        <div className="fixed top-4 right-4">
            <button 
                onClick={toggleMenu} 
                className="p-2 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 focus:outline-none"
            >
                <Icon.User size={20} />
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
                                <span className="ml-2">{ loading === item.id ? <Spinner /> : item.name }</span>
                            </button>
                        ))}
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
};