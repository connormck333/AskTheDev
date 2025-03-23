import { useState } from 'react';
import { Status, User } from '../utils/interfaces';
import { createCheckoutSession } from '../methods/payments/createCheckoutSession';
import Loading from '../components/Loading';

const tiers = [
    {
        name: 'Basic',
        id: 'BASIC',
        priceMonthly: '£3.99',
        description: "The perfect plan if you're just getting started with our product.",
        features: [
            "15 prompts /day",
            "Access to GPT-4o mini"
        ],
        featured: false
    },
    {
        name: 'Pro',
        id: 'PRO',
        priceMonthly: '£9.99',
        description: 'Dedicated support and infrastructure for your company.',
        features: [
            "50 prompts /day",
            "Web page summaries",
            "Access to GPT-4o mini",
            "Access to GPT-4o",
            "Access to OpenAI o3-mini (Best for coding, math and science)"
        ],
        featured: true,
    },
]

interface SubscriptionScreenProps {
    user: User,
}

export default function SubscriptionScreen(props: SubscriptionScreenProps) {

    const { user } = props;
    const [loading, setLoading] = useState<boolean>(false);

    async function startCheckout(tier: any): Promise<void> {
        if (loading) {}
        setLoading(true);

        const response: Status = await createCheckoutSession(user.userId, tier.id);

        setLoading(false);

        if (!response.success) {
            alert("There was an error creating payment intent.");
            return;
        }

        redirectToCheckout(response.data.url);
    }

    async function redirectToCheckout(url: string): Promise<void> {
        window.open(url);
    }

    return (
        <div className="relative isolate bg-white px-6 py-12 sm:py-15 lg:px-8">
            <div className="w-full flex justify-center items-center mb-6">
                <img className="w-auto h-10 mr-2" src="/logo.png" alt="logo"/>
            </div>
            <div className="mx-auto max-w-4xl text-center">
                <p className="mt-2 text-2xl font-semibold text-balance text-gray-900 sm:text-4xl">
                    Choose the right plan for you
                </p>
            </div>
            <div className="mx-auto mt-6 grid max-w-lg grid-cols-1 items-center gap-y-6 sm:mt-20 sm:gap-y-0 lg:max-w-4xl lg:grid-cols-2">
                { tiers.map((tier) => (
                    <div className="flex flex-col p-6 mx-auto max-w-[400px] text-center text-gray-900 bg-white rounded-lg border border-gray-100 shadow dark:border-gray-600 xl:p-8 dark:bg-gray-800 dark:text-white">
                        <h3 className="mb-4 text-2xl font-semibold">{ tier.name }</h3>
                        <p className="font-normal text-gray-500 sm:text-lg dark:text-gray-400">{ tier.description }</p>
                        <div className="flex justify-center items-baseline my-8">
                            <span className="mr-2 text-5xl font-extrabold">{ tier.priceMonthly }</span>
                            <span className="text-gray-500 dark:text-gray-400">/month</span>
                        </div>
                        <ul role="list" className="mb-8 space-y-4 text-left">
                            { tier.features.map(feature => (
                                <li className="flex items-center space-x-3">
                                    <svg className="flex-shrink-0 w-5 h-5 text-green-500 dark:text-green-400" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path></svg>
                                    <span>{ feature }</span>
                                </li>
                            ))}
                        </ul>
                        <button
                            type="button"
                            onClick={() => startCheckout(tier)}
                            className="text-white bg-gradient-to-r from-blue-500 via-blue-600 to-blue-700 hover:bg-gradient-to-br focus:ring-4 focus:outline-none focus:ring-blue-300 dark:focus:ring-blue-800 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2"
                        >
                            <Loading
                                loading={loading}
                            >
                                Subscribe to { tier.name }
                            </Loading>
                        </button>
                    </div>
                ))}
            </div>
        </div>  
    );
}