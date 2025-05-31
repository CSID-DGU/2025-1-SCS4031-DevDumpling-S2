import { View } from 'react-native';
import DumplingLoading from '../../components/common/DumplingLoading';
import { useNavigation } from '@react-navigation/native';

const UserTypeLoading = () => {
    const navigation = useNavigation();

    return (
        <DumplingLoading
            message={["사용자 유형을", "분석 중이에요!"]}
            onLoadingComplete={() => {
                navigation.navigate('UserTypeResult');
            }}
        />
    );
};

export default UserTypeLoading;
