import { ReactElement } from "react";
import Loading from "./Loading";

interface ButtonProps {
    onClick: () => void;
    label: string;
    backgroundColor?: string;
    fontColor?: string;
    loading?: boolean;
}

export default function Button(props: ButtonProps): ReactElement {

    return (
        <button
            onClick={props.onClick}
            className="px-4 py-2 font-sans text-xs font-bold text-center text-gray-900 uppercase align-middle transition-all rounded-md select-none hover:bg-gray-900/10 hover:shadow-lg hover:shadow-gray-900/20 active:bg-gray-900/20 disabled:pointer-events-none disabled:opacity-50 disabled:shadow-none"
            style={{
                backgroundColor: props.backgroundColor || "#FFF",
                color: props.fontColor || "#000"
            }}
            type="button"
        >
            <Loading
                loading={props.loading || false}
            >
                { props.label }
            </Loading>
        </button>
    );
}