import { ReactElement } from "react";

export default function Logo(): ReactElement {

    return (
        <img
            src="/logo.png"
            className="h-auto w-80 rounded-lg pb-10"
        />
    );
}