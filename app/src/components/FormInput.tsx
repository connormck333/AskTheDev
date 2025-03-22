import { ChangeEvent, Dispatch, ReactElement, SetStateAction } from "react";

interface FormInputProps {
    value: [string, Dispatch<SetStateAction<string>>],
    label: string,
    placeholder?: string
    type: string
}

export default function FormInput(props: FormInputProps): ReactElement {

    const [value, setValue] = props.value;

    return (
        <div className="flex flex-col items-start">
            <label className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">{ props.label }</label>
            <input 
                className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                type={props.type}
                value={value}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setValue(e.target.value)}
                placeholder={props.placeholder}
            />
        </div>
    );
}