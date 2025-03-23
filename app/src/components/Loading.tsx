import Spinner from "./Spinner";

interface LoadingProps {
    loading: boolean;
    children: React.ReactNode;
}

export default function Loading(props: LoadingProps) {

    const { loading } = props;

    if (!loading) {
        return props.children;
    }

    return (
        <div className="flex w-full justify-center">
            <Spinner />
        </div>
    );
}