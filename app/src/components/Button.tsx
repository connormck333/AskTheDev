import { ReactElement } from "react";

interface ButtonProps {
    onClick: () => void;
    label: string;
    backgroundColor?: string;
}

export default function Button(props: ButtonProps): ReactElement {

    return (
        <button
            onClick={props.onClick}
            className="px-4 py-2 font-sans text-xs font-bold text-center text-gray-900 uppercase align-middle transition-all rounded-md select-none hover:bg-gray-900/10 active:bg-gray-900/20 disabled:pointer-events-none disabled:opacity-50 disabled:shadow-none"
            style={{
                backgroundColor: props.backgroundColor || "#FFF"
            }}
            type="button"
        >
            { props.label }
        </button>
    );
}