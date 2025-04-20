import { useLoadingContext } from '../contexts/LoadingContext';

// 로딩 상태를 제어 커스텀 훅
export const useLoading = () => {
    const { isLoading, setIsLoading } = useLoadingContext();

    // 로딩 시작
    const showLoading = () => setIsLoading(true);

    // 로딩 종료
    const hideLoading = () => setIsLoading(false);

    const withLoading = async (asyncFunction) => {
        showLoading();
        try {
            return await asyncFunction();
        } finally {
            hideLoading();
        }
    };

    return { isLoading, showLoading, hideLoading, withLoading };
};