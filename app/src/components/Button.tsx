import { ReactElement } from "react";
import Loading from "./Loading";

interface ButtonProps {
    onClick: () => void;
    label: string;
    backgroundColor?: string;
    darkBackgroundColor?: string;
    darkFontColor?: string;
    fontColor?: string;
    loading?: boolean;
}

export default function Button(props: ButtonProps): ReactElement {

    function getBackgroundColor(): string {
        if (isDarkMode()) {
            return props.darkBackgroundColor || "#2b7fff";
        }

        return props.backgroundColor || "#FFF";
    }

    function getFontColor(): string {
        if (isDarkMode()) {
            return props.darkFontColor || "#FFF";
        }

        return props.fontColor || "#000";
    }

    function isDarkMode(): boolean {
        return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
    }

    return (
        <button
            onClick={props.onClick}
            className="px-4 py-2 font-sans text-xs font-bold text-center text-gray-900 uppercase align-middle transition-all rounded-md select-none hover:bg-gray-900/10 active:bg-gray-900/20 disabled:pointer-events-none disabled:opacity-50 disabled:shadow-none"
            style={{
                backgroundColor: getBackgroundColor(),
                color: getFontColor()
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