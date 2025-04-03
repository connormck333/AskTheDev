import { ReactElement } from "react";
import Spinner from "../components/Spinner";

export default function LoadingScreen(): ReactElement {

    return (
        <div className="flex flex-col items-center justify-center min-w-100 min-h-130">
            <Spinner height="h-10" width="w-10"/>
        </div>
    );
}