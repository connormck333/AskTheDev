import { ReactElement, useState } from "react";
import { motion, AnimatePresence } from 'framer-motion';
import Button from "./Button";
import { Model } from "../utils/interfaces";

interface ModelSelectorProps {
    model: [Model, Function];
    models: Model[];
}

export default function ModelSelector(props: ModelSelectorProps): ReactElement {

    const { models } = props;
    const [selectedModel, _setSelectedModel] = props.model;
    const [isOpen, setIsOpen] = useState(false);

    function setSelectedModel(model: Model): void {
        _setSelectedModel(model);
        setIsOpen(false);
    }

    return (
        <>
            <Button
                onClick={() => setIsOpen(!isOpen)}
                label={selectedModel.name}
            />
            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={{ opacity: 0, scale: 0.8 }}
                        animate={{ opacity: 1, scale: 1 }}
                        exit={{ opacity: 0, scale: 0.8 }}
                        transition={{ duration: 0.2 }}
                        className="absolute left-0 bottom-full mb-2 w-56 bg-white rounded-2xl shadow-xl overflow-hidden p-2"
                    >
                        <ul>
                            {models.map((model) => (
                            <li
                                key={model.name}
                                onClick={() => setSelectedModel(model)}
                                className="px-4 py-2 cursor-pointer hover:bg-gray-100 flex flex-col items-start"
                            >
                                <div className="font-semibold text-base">{ model.name }</div>
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