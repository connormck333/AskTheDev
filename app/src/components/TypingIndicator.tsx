import { ReactElement } from 'react';
import { motion } from 'framer-motion';

export default function TypingIndicator(): ReactElement {

    return (
        <div className="flex space-x-1 mt-2">
            {[0, 1, 2].map(index => (
                <motion.span
                    key={index}
                    className="block w-2 h-2 bg-gray-400 rounded-full"
                    transition={{
                        y: {
                            duration: 0.4,
                            repeat: Infinity,
                            repeatType: 'loop',
                            ease: 'easeInOut',
                            delay: index * 0.3
                        }
                    }}
                    animate={{ y: [0, -6, 0] }}
                />
            ))}
        </div>
    );
};