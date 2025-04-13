import { ReactElement, useState } from "react";
import { motion, AnimatePresence } from 'framer-motion';
import Button from "./Button";
import { Model, User } from "../utils/interfaces";
import SubscriptionType from "../utils/SubscriptionType";

interface ModelSelectorProps {
    model: [Model, Function];
    models: Model[];
    user: User | undefined;
}

export default function ModelSelector(props: ModelSelectorProps): ReactElement {

    const { user, models } = props;
    const [selectedModel, _setSelectedModel] = props.model;
    const [isOpen, setIsOpen] = useState(false);

    function setSelectedModel(model: Model): void {
        if (user === undefined) return;

        if (model.proFeature && user.subscriptionType != SubscriptionType.PRO) return;
        _setSelectedModel(model);
        setIsOpen(false);
    }

    return (
        <>
            <Button
                onClick={() => setIsOpen(!isOpen)}
                label={selectedModel.name}
                darkBackgroundColor="#1e5bb9"
            />
            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={{ opacity: 0, scale: 0.8 }}
                        animate={{ opacity: 1, scale: 1 }}
                        exit={{ opacity: 0, scale: 0.8 }}
                        transition={{ duration: 0.2 }}
                        className="absolute left-0 bottom-full mb-2 w-56 bg-white rounded-2xl shadow-xl overflow-hidden"
                    >
                        <ul>
                            {models.map((model) => (
                            <li
                                key={model.name}
                                onClick={() => setSelectedModel(model)}
                                className={
                                    (user === undefined || (model.proFeature && user.subscriptionType != SubscriptionType.PRO))
                                    ? "px-4 py-3 bg-gray-100 flex flex-col items-start"
                                    : "px-4 py-2 cursor-pointer hover:bg-gray-100 flex flex-col items-start"
                                }
                            >
                                <div className="font-semibold text-sm text-black">{ model.name }{ (model.proFeature && user?.subscriptionType !== SubscriptionType.PRO) ? " (PRO)" : "" }</div>
                                <div className="text-sm text-gray-600 text-left">{ model.description }</div>
                            </li>
                            ))}
                        </ul>
                    </motion.div>
                )}
            </AnimatePresence>
        </>
    );
};