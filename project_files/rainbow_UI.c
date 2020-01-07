#include "rainbow_CORE.h"
#include "rainbow_UI.h"
#include "rainbow_IO.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>


void clearScreenBuffer(void)
{
    char c;
    while ((c = getchar()) != EOF && c != '\n');

}

enum UserType showMenu_Welcome(void)
{
    enum UserType userType = undefine;
    int typeInput = 0;
    system("cls");
    while( !(userType==admin || userType==customer) )
    {
        printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
        printf("\t��ѡ���������\n\n\n");
        printf("\t1) ����һ���˿�.\n\n");
        printf("\t2) ����ϵͳ����Ա.\n\n");
        printf("�����������ݶ�Ӧ�����: [ ]\b\b");
        scanf("%d", &typeInput);
        clearScreenBuffer();
        system("cls");
        if( typeInput==1 )
        {
            userType = customer;
            G_myRainbowRestaurant.current_serve_user.userType = customer;
        }
        else if( typeInput==2 )
        {
            printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
            printf("\t���������Ա�����Ա���֤������\n\n");
            printf("\t> ___________________________\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
            char password_input[ADMINPAS_LENGTH_MAX+1];
            //scanf("%s", password_input);
            fscanf(stdin, "%s", password_input);//����ȫ�����뺯����
            clearScreenBuffer();

            if( !(strcmp(password_input, G_myRainbowRestaurant.superAdmin_password)==0) )
            {
                //���벻ƥ��
                printf("���벻��ȷ !\n");
                rainbow_sleep(1000);
                userType = undefine;
                G_myRainbowRestaurant.current_serve_user.userType = undefine;
            }
            else
            {
                userType = admin;
                G_myRainbowRestaurant.current_serve_user.userType = admin;
            }
            system("cls");
        }
    }
    return userType;

}

int showMenu_admin(void)
{
    printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
    printf("\t1) ���ӡ�ɾ��������Ʒ.\n\n");
    printf("\t2) �鿴���޸Ĳ����û���Ϣ.\n\n");
    printf("\t3) �鿴����ʵʱ��λռ�����.\n\n");
    printf("\t4) ��Ʒ���û����ݷ���.\n\n");
    printf("\t5) ϵͳ����Ա��Ϣ�޸�.\n\n");
    printf("\t6) �˳�ϵͳ.\n\n");
    int selection = 0;
    while( !(selection>=1 && selection<=6) )
    {
        printf("�����������Ӧ�����: [ ]\b\b");
        scanf("%d", &selection);
        clearScreenBuffer();
    }
    system("cls");
    return selection;

}

int showMenu_customer(void)
{
    //���֮ǰ��û�ж�����û���������һ�ν�������˵���û��ע�����Ϣ��
    if( 0==strcmp("",G_myRainbowRestaurant.current_serve_user.userNick) )
    {
        struct User user;
        printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
        printf("\t������������֣�30�ַ����ڣ�\n\n\t> __________\b\b\b\b\b\b\b\b\b\b");
        scanf("%s", user.userNick);

        if( isThisUserInUsersHistory(user.userNick) )//�����֮ǰ�������û�
        {
            loadUserDataToCurrentServeUserFromUsersHistory(user.userNick);
        }
        else//��������û�
        {
            printf("\n\t��......\n\t���������ǵ�һ������������.\n");
            printf("\n\t��������Ҫ���˻����ֵ����Ǯ: \n\t(��ֵ����1000Ԫ�Զ���Ϊ����VIP��ȫ��8��Ŷ)\n");
            printf("\t> __________\b\b\b\b\b\b\b\b\b\b");
            scanf("%d", &user.moneyInAccount);
            if(user.moneyInAccount >= 1000){
                user.IsVIP = 1;
            }else{
                user.IsVIP = 0;
            }
            user.order.foodCount = 0;
            user.order.ExpensesToBePaid = 0;
            user.userType = customer;
            user.seat.isSelected = 0;
            G_myRainbowRestaurant.current_serve_user = user;
        }
        updateUserInfoToHistoryAndFile(user);
        system("cls");
    }
    
    printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
    printf("�����û� %s \n\n", G_myRainbowRestaurant.current_serve_user.userNick);
    if( G_myRainbowRestaurant.current_serve_user.IsVIP )
    {
        printf("���Ǳ�������VIP�����в�Ʒ8���Ż�\n");
    }
    printf("\n");
    printf("\t1) ��ѡһ����λ����.\n\n");
    printf("\t2) ����������ʲô��Ʒ.\n\n");
    printf("\t3) ��Ҫ��һЩ�Ե�.\n\n");
    printf("\t4) ��Ҫ��ȥ�Է���.\n\n");
    printf("\t5) �Ա��ˣ���Ҫ����.\n\n");
    printf("=======================================\n");
    if( G_myRainbowRestaurant.current_serve_user.order.foodCount > 0 )
    {//������˲;�չʾ��Ĳ͵������ͷ���
        printf("��Ŀǰ������ %d ����.\n", G_myRainbowRestaurant.current_serve_user.order.foodCount);
        printf("��Ҫ���ѽ��: %d Ԫ\n",G_myRainbowRestaurant.current_serve_user.order.ExpensesToBePaid);
    }
    printf("����ǰ�˻������: %d\n", G_myRainbowRestaurant.current_serve_user.moneyInAccount);
    printf("=======================================\n\n");
    int selection = 0;
    while( !(selection>=1 && selection<=5) )
    {
        printf("�����������Ӧ�����: [ ]\b\b");
        scanf("%d", &selection);
        clearScreenBuffer();
    }
    system("cls");
    return selection;

}

//�û����� ��ʼ###########################################################
void showSeatSelectMenu(void)
{
    printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
    printf("���ǲ�����ʵʱ��λ���ͼ.\n");
    printSeatsMap();
    printf("\n�����԰��� [�����],[�����] \n�������ʽ��ѡ��һ����λ.\n\n");
    int row, col;
    while(1)
    {
        printf("��Ҫѡ��λ��: ");
        scanf("%d,%d", &row, &col);
        row -= 1;
        col -= 1;

        //�ж���λ�Ƿ��ѡ��
        if( (col>=0 && col<SEATINFO_WIDTH) && (row>=0 && row<SEATINFO_HEIGHT) )
        {
            int isSeatAvailable = !(G_myRainbowRestaurant.seatsMap[row][col].isSelected);
            int self_row, self_col;
            self_row = G_myRainbowRestaurant.current_serve_user.seat.row;
            self_col = G_myRainbowRestaurant.current_serve_user.seat.col;
            int isSelfSeat = (row==self_row) && (col==self_col);
            
            if( isSelfSeat )//������Լ�֮ǰѡ����λ
            {
                break;
            }

            if( isSeatAvailable )//����Ǹ�����λ
            {
                break;
            }
            else//����ѡ������λ
            {
                printf("���λ���Ѿ�������Ŷ~\n");
                rainbow_sleep(1000);
            }
            
        }
        clearScreenBuffer();
    }
    if( G_myRainbowRestaurant.current_serve_user.seat.isSelected == 1 )
    {//���ԭ��ѡ�����λ����Ҫ������λ
        int old_row, old_col;
        old_row = G_myRainbowRestaurant.current_serve_user.seat.row;
        old_col = G_myRainbowRestaurant.current_serve_user.seat.col;
        G_myRainbowRestaurant.seatsMap[old_row][old_col].isSelected = 0;//ȡ��ѡ�����λ
    }
    G_myRainbowRestaurant.seatsMap[row][col].isSelected = 1;//ѡ���û��������λ
    
    /*//�±������а���˵�ǲ���д�ģ���Ϊ����database_seatMap�ļ���ʱ��
      //��Ӧλ�õ�seat�ṹ���row��col�������ݶ�Ӧ����д���˵�
      //����Ҳ��ʵ����û��д�ã����������ü���ˣ��ͼ����������޲�һ��?
      //G_myRainbowRestaurant.seatsMap[row][col].row = row;
      //G_myRainbowRestaurant.seatsMap[row][col].col = col;
    �ٺٺٽ���ˣ�����Ļ�����û˵*/
    G_myRainbowRestaurant.current_serve_user.seat = G_myRainbowRestaurant.seatsMap[row][col];
    writeSeatMapInfoToFile((struct Seat *)&G_myRainbowRestaurant.seatsMap);
    updateUserInfoToHistoryAndFile(G_myRainbowRestaurant.current_serve_user);
    printf("��ѡ���˵� %d �ŵ� %d �е���λ.\n", row+1, col+1);
    rainbow_sleep(1000);
    system("cls");

}

void showFoodMenu(void)
{
    printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
    printf("���\t ����\t                ����\n");
    printf("=======================================\n\n");
    printLinkTable(G_myRainbowRestaurant.foods_we_have);
    printf("=======================================\n\n");
    printf("��Enter�������û���һ���˵�...");
    getchar();
    system("cls");
    
}

void showOrderMenu(void)
{
    struct Order order;
    order.foodCount = 0;
    order.ExpensesToBePaid = 0;
    order.foods = NULL;
    NODE *pHead = NULL;
    
    while(1)
    {
        printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
        int i;
        if( !(order.foodCount==0) )
        {
            printf("�������Ѿ���Ĳ�Ʒ\n");
            printf("���\t ����\t                ����\n");
        }
        printf("===========================================\n\n");
        for(i=0; i<order.foodCount; i++)
        {
            printf("%d\t %-15s\t%-4dRMB\n",order.foods[i].id,order.foods[i].name,order.foods[i].price);
        }
        printf("\n===========================================\n");
        printf("�ܲ�Ʒ��: %2d     Ӧ֧�����: %5d\n", order.foodCount, order.ExpensesToBePaid);
        printf("===========================================\n\n");
        printf("�����������Ĳ˶�Ӧ�����, \n���� 0 �˳�������һ���˵�\n\n");
        static int addResult = -1;//��ֵ����������0
        if( !addResult )//�����һ������˷Ƿ�foodID������һ�����ʧ�ܣ����������ʾ
        {
            printf("�����Ų����ڣ���������ȷ�Ĳ�Ʒ���.\n");
        }
        int foodID;
        printf("��Ʒ���: [ ]\b\b");
        scanf("%d", &foodID);
        clearScreenBuffer();
        system("cls");
        if( foodID==0 ) 
        {
            break;
        }
        else
        {
            addResult = addFoodToOrder(foodID, &order);
            /*if( !result )
            {
                printf("Wrong food id ! Please select the food exist.\n");
            }*/
        }
    }

    G_myRainbowRestaurant.current_serve_user.order = order;
    updateUserInfoToHistoryAndFile(G_myRainbowRestaurant.current_serve_user);

}

void showCheckMenu(void)
{
    printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
    int fee;//����Ӧ������
    int isVIP = G_myRainbowRestaurant.current_serve_user.IsVIP;
    if( isVIP )
    {
        fee = 0.8 * G_myRainbowRestaurant.current_serve_user.order.ExpensesToBePaid;
    }
    else
    {
        fee = G_myRainbowRestaurant.current_serve_user.order.ExpensesToBePaid;
    }
    printf("���ܹ���Ҫ֧�� %5d Ԫ %s.\n", fee, isVIP?"(����VIP���Ѵ�8��)":"");
    int moneyLeft = G_myRainbowRestaurant.current_serve_user.moneyInAccount-fee;
    printf("֧���������ʻ�����Ϊ %d Ԫ.\n", moneyLeft);

    if( moneyLeft>=0 )
    {
        printf("��ɨ���±ߵĶ�ά����ȷ�ϱ���֧��\n\n");
        printf("\t   �� ���� ������ �� �� ��  \n");
        printf("\t   ���ء��� �������� �ء�   \n");
        printf("\t   �� ���� ��  ���� ������  \n");
        printf("\t    ���� ���� �������� ����  \n");
        printf("\t   ������ �������� �� �� ��  \n");
        printf("\t   ���ء����� ��  ���ء�   \n");
        printf("\t   ���� ���� ���� �� �� ��  \n");
        //����˵�
        G_myRainbowRestaurant.current_serve_user.order.ExpensesToBePaid = 0;
        G_myRainbowRestaurant.current_serve_user.order.foodCount = 0;
        //��Ǯ��
        G_myRainbowRestaurant.current_serve_user.moneyInAccount -= fee;
        updateUserInfoToHistoryAndFile(G_myRainbowRestaurant.current_serve_user);
    }
    else
    {
        printf("\nȻ�������˻��е�������֧���������òͲ����ķ���.\n\n");
        printf("��������3��ѡ��:\n\n");
        printf("\t1) ���˻��г�ֵ.\n\n");
        printf("\t2) ������ϴ����.\n\n");
        printf("\t3) ��Ѿ�ӿ���.\n\n");
        int selection = 0;
        while ( !(selection>=1 && selection<=3) )
        {
            printf("�����������Ӧ�����: [ ]\b\b");
            scanf("%d", &selection);
            clearScreenBuffer();
        }
        if( selection==1 )//��ֵ
        {
            int moneyAdd = 0;
            while (1)
            {
                printf("��������Ҫ���˻����ֵ����Ǯ: \n");
                printf("> __________\b\b\b\b\b\b\b\b\b\b");
                scanf(" %d", &moneyAdd);
                if( moneyAdd>0 )
                {
                    G_myRainbowRestaurant.current_serve_user.moneyInAccount += moneyAdd;
                    updateUserInfoToHistoryAndFile(G_myRainbowRestaurant.current_serve_user);
                    printf("��ֵ�ɹ���\n");
                    printf("�����ʻ���ǰ���: %d\n", G_myRainbowRestaurant.current_serve_user.moneyInAccount);
                    rainbow_sleep(1000);
                    system("cls");
                    return;//������һ���˵�
                }
            }
        }
        else if( selection==2 )//ϴ����
        {
            int dishToWashNum = fee;//Ƿ1��Ǯϴһ��
            printf("\n����Ҫϴ%d������\n\n", dishToWashNum);
            printf("��ʼ����\n");
            int i;
            for(i=1; i<=dishToWashNum; i++)
            {
                printf("\t��������ϴ�� %d ������...", i);
                rainbow_sleep(1);//ÿ��ϴ1000������
                printf("\r");
            }
            G_myRainbowRestaurant.current_serve_user.order.ExpensesToBePaid = 0;
            G_myRainbowRestaurant.current_serve_user.order.foodCount = 0;
            printf("\n��ϲ����������й��������βͷѵ���\n");
            printf("\n\n�������������һ���˵�.\n");
            getchar();
            system("cls");
            return;//������һ���˵�
        }
        else if( selection==3 )//��·
        {
            printf("\tϵͳ���Զ�������\n");
            int i;
            for(i=3; i>=0; i--)
            {
                printf("\t���컹�� %d �뵽���ֳ�...", i);
                rainbow_sleep(1000);
                printf("\r");
            }
            printf("\n\t�㱻����ץ���� ��\n\n");
            printf("\n\n�������������һ���˵�.\n");
            getchar();
            system("cls");
            return;
        }
        system("cls");
    }
    
    //ȡ����λѡ��
    int customer_row = G_myRainbowRestaurant.current_serve_user.seat.row;
    int customer_col = G_myRainbowRestaurant.current_serve_user.seat.col;
    G_myRainbowRestaurant.seatsMap[customer_row][customer_col].isSelected = 0;
    writeSeatMapInfoToFile((struct Seat *)&G_myRainbowRestaurant.seatsMap);

    printf("\n��������˳�.\n");
    getchar();
    system("cls");
    printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
    printf("\n\tף���ò���� !\n\n\t��ӭ���´ι���\n\n\n");
    rainbow_sleep(1000);
    exit(EXIT_SUCCESS);

}
//�û����� ����###########################################################

//����Ա���� ��ʼ###########################################################
void showUsersInfo_admin(void)
{
    while(1)
    {
        printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
        printf("�û���\t�˻����\t�Ƿ�VIP\t�����ò�\n\n");
        int i;
        struct User user;
        for(i=0; i<G_myRainbowRestaurant.historyUsersCount; i++)
        {
            user = G_myRainbowRestaurant.history_serve_users[i]; 
            printf("%-8s%-20d%-9s%-9s\n", user.userNick, user.moneyInAccount, user.IsVIP?"��":"��", user.seat.isSelected?"��":"��");
        }
        printf("\n--------------------------------------\n");
        printf("���� \"set [�û���] [�˻����]\" �����ù˿��˻����\n");
        printf("���� \"del [�û���]\" ��ɾ���˿���Ϣ\n");
        printf("���� \"quit\" to ������һ���˵�\n\n");
        printf("@admin > ");
        {
            char cmd[30];
            scanf("%s", cmd);
            if( strcmp(cmd, "set")==0 )
            {
                char nickName[USERNICK_LENGTH_MAX+1];
                int money;
                scanf("%s", nickName);
                scanf(" %d", &money);
                setUserMoneyFromUsersHistory(nickName, money);
            }
            else if( strcmp(cmd, "del")==0 )
            {
                char nickName[USERNICK_LENGTH_MAX+1];
                scanf("%s", nickName);
                if( isThisUserInUsersHistory(nickName) )
                {
                    deleteUserFromUsersHistory(nickName);
                }
                else
                {
                    printf("����û������ڣ��������룡\n");
                    rainbow_sleep(1000);
                }
                
            }
            else if( strcmp(cmd, "quit")==0 )
            {
                clearScreenBuffer();
                system("cls");
                break;
            }
            else
            {
                printf("�޷�ʶ�������");
                rainbow_sleep(500);
            }
        }
        clearScreenBuffer();
        system("cls");
    }


}

void showUsersManagement_CURD_admin(void)
{
    printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
    printf("ϵͳ�е�ǰ�� 1 ������Ա.\n\n");
    printf("\t1) �޸Ĺ���Ա����.\n\n");
    printf("\t2) ������һ���˵�.\n\n");
    int selection = 0;
    while ( !(selection>=1 && selection<=2) )
    {
        printf("�����������Ӧ�����: [ ]\b\b");
        scanf("%d", &selection);
        clearScreenBuffer();
    }
    if( selection==1 )
    {
        char password[ADMINPAS_LENGTH_MAX+1];
        printf("�������µ�����.");
        scanf("%s", password);
        writeSuperAdminPasswordToFile(password);
        printf("�޸ĳɹ���");
        rainbow_sleep(1000);
    }
    else if( selection==2 )
    {
        ;
    }
    system("cls");

}

void showFoodsManagement_CURD_admin(void)
{
    while (1)
    {
        printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
        printf("���\t ��Ʒ��\t                ����\n");
        printf("=======================================\n\n");
        printLinkTable(G_myRainbowRestaurant.foods_we_have);
        printf("=======================================\n\n");
        printf("���� \"add [��Ʒ��] [����]\" ����Ӳ�Ʒ\n");
        printf("���� \"del [���]\" ��ɾ����Ʒ\n");
        printf("���� \"quit\" to ������һ���˵�\n\n");
        printf("@admin > ");
        {
            char cmd[30];
            scanf("%s", cmd);
            if( strcmp(cmd, "add")==0 )
            {
                //����ʳ������
                char name[30];
                int price;
                int id;
                scanf(" %s", name);
                scanf(" %d", &price);
                id = getAvailableFoodID();
                
                //����ʳ��ṹ��
                struct food newFood;
                newFood.id = id;
                strcpy(newFood.name, name);
                newFood.price = price;
                newFood.orderCount = 0;

                //����ʳ�����ݵ�ϵͳ���Ҵ���
                addFoodToRestaurant(&newFood);
                G_myRainbowRestaurant.foodsCount++;
                saveFoodInfo();

            }
            else if( strcmp(cmd, "del")==0 )
            {
                if( !(0==G_myRainbowRestaurant.foodsCount) )//�������ʳ���ɾ
                {
                    int id;
                    scanf(" %d", &id);
                    G_myRainbowRestaurant.foods_we_have = deleteRestaurantFoodByID(G_myRainbowRestaurant.foods_we_have, id);
                    G_myRainbowRestaurant.foodsCount--;
                    saveFoodInfo();
                }
            }
            else if( strcmp(cmd, "quit")==0 )
            {
                clearScreenBuffer();
                system("cls");
                break;
            }
            else
            {
                printf("�޷�ʶ�������");
                rainbow_sleep(500);
            }
        }
        clearScreenBuffer();
        system("cls");
    }

}

void showSeatMapInfo_admin(void)
{
    printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
    printf("���ǲ���ʵʱ��λͼ.\n");
    printSeatsMap();
    int customer_count = 0;
    int i, j;
    for(i=0; i<SEATINFO_HEIGHT; i++)
    {
        for(j=0; j<SEATINFO_WIDTH; j++)
        {
            if( G_myRainbowRestaurant.seatsMap[i][j].isSelected )
            {
                customer_count++;
            }
        }
    }
    printf("=======================================\n");
    printf("��ǰ�������� %d λ�˿�.\n", customer_count);
    printf("=======================================\n");
    printf("��Enter������һ���˵�...");
    getchar();
    system("cls");

}


void showStatisticsAnalysis_admin(void)
{
    printf("########## �ӱ���ѧ �ʺ���� #########\n");
    printf("\n�����ǵ�ǰ���ܹ˿ͻ�ӭ�� 3 ����Ʒ.��\n");
    printPopularFood(3);
    printf("\n�����ǵ�ǰ�˻�������� 3 ���˿�.��\n");
    printRichUser(3);

    //���������ǰ�˿͸������˿��˻�������ܺ�
    int i, j, MoneySum = 0;
    for(i=0; i<G_myRainbowRestaurant.historyUsersCount; i++)
    {
        MoneySum += G_myRainbowRestaurant.history_serve_users[i].moneyInAccount;
    }

    //���������λ������
    int current_customer_count = 0;
    for(i=0; i<SEATINFO_HEIGHT; i++)
    {
        for(j=0; j<SEATINFO_WIDTH; j++)
        {
            if( G_myRainbowRestaurant.seatsMap[i][j].isSelected )
            {
                current_customer_count++;
            }
        }
    }
    int totalSeatsNum = SEATINFO_HEIGHT*SEATINFO_WIDTH;
    float occupancyRate = (float)current_customer_count / totalSeatsNum;
    
    printf("\n���������ݡ�\n");
    printf("---------------------------------------\n");
    printf("��ǰ�����û�������%d ��\n", G_myRainbowRestaurant.historyUsersCount);
    printf("��ǰ���������ʣ�%.1f%% (%d/%d)\n", occupancyRate*100, current_customer_count, totalSeatsNum);
    printf("��ǰ�������ʲ���%d Ԫ\n", MoneySum);
    printf("---------------------------------------\n\n\n");
    printf("��Enter������һ���˵�...");
    getchar();
    system("cls");

}
//����Ա���� ����###########################################################

void showExitProgram(void)
{
    printf("########## �ӱ���ѧ �ʺ���� #########\n\n");
    if( G_myRainbowRestaurant.current_serve_user.userType == customer )
    {
        printf("�𾴵Ĺ˿ͣ�ף���ò���죡\n\n");
        printf("�������Ժ��������ˡ�\n\n");
    }
    else if( G_myRainbowRestaurant.current_serve_user.userType == admin )
    {
        printf("�𾴵�ϵͳ����Ա�����Ѱ�ȫ�˳�ϵͳ��\n\n");
    }
    else
    {
        printf("\t�����˳�ϵͳ��\n\n");
    }
    rainbow_sleep(2000);
    exit(EXIT_SUCCESS);
}
