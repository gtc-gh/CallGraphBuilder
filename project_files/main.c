#include "rainbow_CORE.h"
#include "rainbow_IO.h"
#include "rainbow_UI.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main(int argc, char const *argv[])
{
    //�����ļ���ȡ��ȫ�ֱ�����ʼ��
    rainbow_init(argc, argv);
    //���û�ѡ���Լ������
    enum UserType userType = showMenu_Welcome();
    if( userType==admin )//��ǰ�û��ǹ���Ա
    {
        while(1)
        {
            int selection = showMenu_admin();
            switch (selection)
            {
                case 1://���ӡ�ɾ��������Ʒ.
                    showFoodsManagement_CURD_admin();
                    break;
                case 2://�鿴���޸Ĳ����û���Ϣ.
                    showUsersInfo_admin();
                    break;
                case 3://�鿴����ʵʱ��λռ�����.
                    showSeatMapInfo_admin();
                    break;
                case 4://��Ʒ���û����ݷ���.
                    showStatisticsAnalysis_admin();
                    break;
                case 5://ϵͳ����Ա��Ϣ�޸�.
                    showUsersManagement_CURD_admin();
                    break;
                case 6://�˳�����.
                    showExitProgram();
                    break;
            }
        }
    }
    else if( userType==customer )//��ǰ�û��ǹ˿�
    {
        while(1)
        {
            int selection = showMenu_customer();
            switch (selection)
            {
                case 1://��ѡһ����λ����.
                    showSeatSelectMenu();
                    break;
                case 2://����������ʲô��Ʒ.
                    showFoodMenu();
                    break;
                case 3://��Ҫ��һЩ�Ե�.
                    showOrderMenu();
                    break;
                case 4://��Ҫ��ȥ�Է���.
                    showExitProgram();
                    break;
                case 5://�Ա��ˣ���Ҫ����.
                    showCheckMenu();
                    break;
            }
        }
    }
    
    return 0;
}



