import { ReactElement } from "react";
import Loading from "./Loading";

interface GradientButtonProps {
    onClick: Function;
    label: string;
    loading?: boolean;
}

export default function GradientButton(props: GradientButtonProps): ReactElement {

    return (
        <button
            type="button"
            onClick={() => props.onClick()}
            className="text-white bg-gradient-to-r from-blue-500 via-blue-600 to-blue-700 hover:bg-gradient-to-br focus:ring-4 focus:outline-none focus:ring-blue-300 dark:focus:ring-blue-800 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2"
        >
            <Loading
                loading={props.loading || false}
            >
                { props.label }
            </Loading>
        </button>
    );
}