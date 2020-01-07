#include "rainbow_CORE.h"
#include "rainbow_IO.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <windows.h>


int rainbow_init(int argc, char const *argv[])
{
    int i;
    for(i=1; i<argc; i++)
    {
        if( 0==strcmp("/init",argv[i]) )//����ȫ�µĳ�ʼ�����ļ��������ݽ���ʧ����
        {
            createFreshDataBaseFiles();
            printf("�Ѿ��������µ������ļ�");
            exit(EXIT_SUCCESS);
        }
        else if( 0==strcmp("/author",argv[i]) )//��ʾ������Ϣ
        {
            printf("�ӱ���ѧ ����ѧԺ 2019�� ��Ϣ��ȫ1�� ��ī�");
            exit(EXIT_SUCCESS);
        }
    }
    //���ÿ���̨ǰ/����ɫ
    //system("COLOR F0");
    //���ÿ���̨��С
    system("mode con cols=50 lines=30");
    //����ʳ������
    int num;
    struct food *foods_read;
    readFoodInfoFromFile(&foods_read, &num); 
    NODE *pHead = loadFoodInfoToLinkTable(foods_read, num);
    G_myRainbowRestaurant.foods_we_have = pHead;
    G_myRainbowRestaurant.foodsCount = num;

    //�����û�����
    struct User *users_read;
    readUserInfoFromFile(&users_read, &num);
    G_myRainbowRestaurant.history_serve_users = users_read;
    G_myRainbowRestaurant.historyUsersCount = num;

    //��ȡ����Ա����
    char *pPassword = readSuperAdminPasswordFromFile();
    strcpy(G_myRainbowRestaurant.superAdmin_password, pPassword);

    //��ʼ����ǰ�����û��ṹ���Ҫ��Ϣ
    strcpy(G_myRainbowRestaurant.current_serve_user.userNick, "");
    G_myRainbowRestaurant.current_serve_user.seat.isSelected = 0;
    G_myRainbowRestaurant.current_serve_user.seat.row = -1;
    G_myRainbowRestaurant.current_serve_user.seat.col = -1;
    G_myRainbowRestaurant.current_serve_user.userType = undefine;

    //������λͼ��Ϣ
    readSeatMapInfoFromFile((struct Seat *)&G_myRainbowRestaurant.seatsMap);
    return 0;
}


int isTwoFoodEqual(struct food food1, struct food food2)
{
    return (food1.id==food2.id) && 
            (strcmp(food1.name, food2.name)==0) && 
            (food1.price==food2.price);
}

//������ز��� ��ʼ##############################################################
NODE *addNodeByOrder(NODE *pHead, struct food data)
{
    //���ݴ������ݴ���һ���µ�����ڵ�
    NODE *pNode = (NODE *)malloc(sizeof(NODE));
    if_Is_Null_Then_End_Program(pNode);
    pNode->data = data;
    pNode->next = NULL;

    if( pHead==NULL )//���Ҫ��ӵ�һ�� ������ ��
    {
        pNode->next = pHead;
        pHead = pNode;
    }
    else//���Ҫ��ӵ�һ�� �ǿ����� ��
    {
        if( pNode->data.id<pHead->data.id )//���Ӧ����ӵ��ǿ������ ��һλ ��
        {
            pNode->next = pHead;
            pHead = pNode;
        }
        else//���Ӧ����ӵ��ǿ������ �ǵ�һλ ��
        {
            NODE *pr = pHead;
            while ( !(pr==NULL) )//��ͼ��������
            {
                if( pr->data.id<pNode->data.id )//���п��ܡ�����������½��õ���λ������
                {   
                    if( pr->next==NULL )//�����ǰλ�� �� �������һλ
                    {
                        //ֱ����������ӵ�����β��
                        pNode->next = pr->next;
                        pr->next = pNode;
                        break;
                    }
                    else//�����ǰλ�� ���� �������һλ
                    {
                        if(pr->next->data.id > pNode->data.id)//��ĵ���������½��õ���λ������
                        {
                            //����½�㵽���λ����
                            pNode->next = pr->next;
                            pr->next = pNode;
                            break;
                        }
                    }
                }
                pr = pr->next;//��������ڵ�
            }
        }
    }
    //һ��Ҫ�����µ�����ͷ��ַ����Ϊ�п�������ͷ��ַ���ϱߴ����б��޸���
    return pHead;//�����µ�����ͷ
}

NODE *addNodeToTail(NODE *pHead, struct food data)
{
    //���ݴ������ݴ���һ���µ�����ڵ�
    NODE *pNode = (NODE *)malloc(sizeof(NODE));
    if_Is_Null_Then_End_Program(pNode);
    pNode->data = data;
    pNode->next = NULL;

    if( pHead==NULL )//���Ҫ��ӵ�һ�� ������ ��
    {
        pNode->next = pHead;
        pHead = pNode;
    }
    else//���Ҫ��ӵ�һ�� �ǿ����� ��
    {
        NODE *pr=NULL;
        pr = pHead;
        while ( !( pr->next==NULL ) )//����������β��
        {
            pr = pr->next;
        }
        pr->next = pNode;//����½�㵽����β��
    }
    return pHead;
}

NODE *addNodeToHead(NODE *pHead, struct food data)
{
    //���ݴ������ݴ���һ���µ�����ڵ�
    NODE *pNode = (NODE *)malloc(sizeof(NODE));
    if_Is_Null_Then_End_Program(pNode);
    pNode->data = data;
    pNode->next = NULL;

    //����½�㵽����ͷ��
    pNode->next = pHead;
    pHead = pNode;
    return pHead;
}

NODE *deleteNodeByValue(NODE *pHead, struct food data)
{
    if( pHead==NULL ) //�ձ���ֱ���˳�����
    {
        return NULL;
    }

    //if( pHead->data == data ) //�����ڱ�ͷ���������⴦��
    if( isTwoFoodEqual(pHead->data, data) )
    {
        NODE *pDelete = pHead;
        pHead = pHead->next;
        free(pDelete);
    }
    else // �����ڱ�ͷ������
    {
        NODE *pLast = pHead;
        NODE *pNext = pHead->next;
        NODE *pDelete = NULL;
        while ( !(pNext->next==NULL) )//��ͼ��������
        {
            pLast = pLast->next;
            pNext = pNext->next;
            //if( pNext->data==data )
            if( isTwoFoodEqual(pHead->data, data) )//����ҵ�
            {
                //ɾ��
                pDelete = pNext;
                pLast->next = pNext->next;
                free(pDelete);
                //����ֱ��break��ֻ��ɾ����һ��ƥ��Ľڵ�
                break;
            }
        }
        
        
    }
    return pHead;
}

NODE *deleteRestaurantFoodByID(NODE *pHead, int id)
{
    if( pHead==NULL ) //�ձ�
    {
        return NULL;
    }
    
    //if( pHead->data == data ) //�����ڱ�ͷ���������⴦��
    if( id==pHead->data.id )
    {
        NODE *pDelete = pHead;
        pHead = pHead->next;
        free(pDelete);
    }
    else // �����ڱ�ͷ������
    {
        NODE *pLast = pHead;
        NODE *pNext = pHead->next;
        NODE *pDelete = NULL;
        while (1)
        {
            //if( pNext->data==data )
            if( id==pNext->data.id )//����ҵ�
            {
                pDelete = pNext;
                pLast->next = pNext->next;
            }

            if(pNext->next==NULL)//������������β
            {
                break;
            }
            else
            {
                pLast = pLast->next;
                pNext = pNext->next;
            }
            /* ����һ��Ҫ���ж��ҵ����ݣ��ٽ�pLast��pNext�����ƶ���
            ˳���ܷ�����������ڶ������ݾͻᱻ���Թ�ȥ��������Ϊʲô�ǵ�д���ע��(��_��|||) */
        }
        free(pDelete);
    }
    return pHead;
}

void printLinkTable(NODE *pHead)
{
    NODE *pr=NULL;
    for(pr=pHead; !(pr==NULL); pr=pr->next)//��������
    {
        //printf("%d ", pr->data);
        printf("%d\t %-15s\t%-4d Ԫ\n", pr->data.id, pr->data.name, pr->data.price);
    }
    printf("\n");
}

NODE *loadFoodInfoToLinkTable(struct food *foods, int num)
{
    NODE *pHead = NULL;
    int i;
    for(i=0; i<num; i++)
    {
        pHead = addNodeToTail(pHead, foods[i]);
    }
    return pHead;
    
}

struct food *convertLinkTableToFoodInfo(NODE *pHead, int num)
{
    struct food *foods = NULL;
    foods = (struct food *)malloc(num*sizeof(struct food));
    if_Is_Null_Then_End_Program(foods);
    NODE *pr=NULL;
    int i=0;
    for(pr=pHead; !(pr==NULL); pr=pr->next)//��������
    {
        foods[i] = pr->data;
        i++;
    }
    return foods;
}
//������ز��� ����##############################################################


int addFoodToOrder(int foodID, struct Order *order)
{
    NODE *pHead = G_myRainbowRestaurant.foods_we_have;
    NODE *pr;

    pr=pHead;
    int i;
    for(i=0; i<G_myRainbowRestaurant.foodsCount; i++)//���������������е�����ʳ��
    {
        if( foodID==pr->data.id )//û��Ҫ�ľ������ʳ��
        {
            //���ʳ���ֱ������һ�Σ���¼����
            pr->data.orderCount++;
            saveFoodInfo();

            //Ϊ��������ٿ���һ��ռ��Ա������µ���һ��food�ṹ��
            int newSize = (order->foodCount+1) * sizeof(struct food);
            order->foods = (struct food *)realloc(order->foods, newSize);
            if_Is_Null_Then_End_Program(order->foods);

            //�����������
            order->foods[(order->foodCount+1)-1] = pr->data;
            order->foodCount++;
            order->ExpensesToBePaid += pr->data.price;
            return 1;
        }
        pr = pr->next;
    }
    /*while(1)
    {
        if( !(pr==NULL) )
        {
            printf("%d\n", pr->data.id);
            if( foodID==pr->data.id )
            {
                order->foods[order->foodCount] = pr->data;
                order->foodCount++;
                order->ExpensesToBePaid += pr->data.price;
                return 1;
            }
            pr = pr->next;
        }
        else
        {
            break;
        }
        
    }*/
    //for(pr=pHead; !(pr==NULL); pr=pr->next)
    //{
    //    printf("%d\n", pr->data.id);
    //    if( foodID==pr->data.id )
    //    {
    //        order->foods[order->foodCount] = pr->data;
    //        order->foodCount++;
    //        order->ExpensesToBePaid += pr->data.price;
    //        return 1;
    //    }
    //    //printf("%d\t %-15s\t%-4dRMB\n", pr->data.id, pr->data.name, pr->data.price);
    //}
    return 0;
}

void addUserToHistory(struct User user)
{
    struct User *users = G_myRainbowRestaurant.history_serve_users;
    int num = 1 + G_myRainbowRestaurant.historyUsersCount;

    int newSize = (num) * sizeof(struct User);
    users = (struct User *)realloc(users, newSize);
    if_Is_Null_Then_End_Program(users);
    users[num-1] = user;

    G_myRainbowRestaurant.history_serve_users = users;
    G_myRainbowRestaurant.historyUsersCount = num;

    writeUserInfoToFile(G_myRainbowRestaurant.history_serve_users, num);

}

void addFoodToRestaurant(struct food *newFood)
{
    //G_myRainbowRestaurant.foods_we_have = addNodeToTail(G_myRainbowRestaurant.foods_we_have, *newFood);
    G_myRainbowRestaurant.foods_we_have = addNodeByOrder(G_myRainbowRestaurant.foods_we_have, *newFood);
} 

void saveFoodInfo(void)
{
    struct food *foods;
    //������ת��������
    foods = convertLinkTableToFoodInfo(G_myRainbowRestaurant.foods_we_have, G_myRainbowRestaurant.foodsCount);
    //�����������
    writeFoodInfoToFile(foods, G_myRainbowRestaurant.foodsCount);
}

int getAvailableFoodID(void)
{
    int id;
    if( G_myRainbowRestaurant.foodsCount==0 )
    {
        id = 1;
    }
    else
    {   if( G_myRainbowRestaurant.foods_we_have->data.id > 1 )//�����һ��Ԫ��id����1
        {
            id = 1;//����һ��1
        }
        else
        {
            NODE *pr=NULL;
            for(pr=G_myRainbowRestaurant.foods_we_have; !(pr==NULL); pr=pr->next)
            {
                if(pr->next==NULL)//����������һ��Ԫ��
                {
                    id = 1 + pr->data.id;//���ɱ����һ��Ԫ��id��1��id
                    break;
                }
                else//��û�����һ��Ԫ��
                {
                    if( ((pr->next)->data.id - pr->data.id)>1 )//���ĳ����Ԫ�ص�id����������
                    {
                        id = 1 + pr->data.id;//���ɱ�ǰһ��Ԫ��id��1��id
                        break;
                    }
                }
            }
        }
    }
    return id;
}

void printSeatsMap(void)
{
    int row,col;
    printf("   ");
    for(row=0; row<SEATINFO_WIDTH; row++)
    {
        printf("%3d", row+1);
    }
    printf("\n");
    for(row=0; row<SEATINFO_HEIGHT; row++)
    {
        printf("%3d ", row+1);
        for(col=0; col<SEATINFO_WIDTH; col++)
        {
            char ch;
            if( !G_myRainbowRestaurant.seatsMap[row][col].isSelected )
            {
                ch = ' ';
            }
            else
            {
                int currentUser_row = G_myRainbowRestaurant.current_serve_user.seat.row;
                int currentUser_col = G_myRainbowRestaurant.current_serve_user.seat.col;
                int isCurrentUserPos = (currentUser_row==G_myRainbowRestaurant.seatsMap[row][col].row) && 
                                        (currentUser_col==G_myRainbowRestaurant.seatsMap[row][col].col);
                if( isCurrentUserPos )//����� ��ǰ�˿� ֮ǰѡ����λ
                {
                    ch = '@';
                }
                else//����� �����˿� ֮ǰѡ����λ
                {
                    ch = '*';
                }
                
            }
            printf("[%c]", ch);
        }
        printf("\n");
    }
}

int isThisUserInUsersHistory(char userNick[])
{
    int i;
    for(i=0; i<G_myRainbowRestaurant.historyUsersCount; i++)//������ʷ�û�����
    {
        if( 0 == strcmp(userNick, G_myRainbowRestaurant.history_serve_users[i].userNick) )
        {
            return 1;
        }
    }
    return 0;
}

void setUserMoneyFromUsersHistory(char userNick[], int money)
{
    int i;
    for(i=0; i<G_myRainbowRestaurant.historyUsersCount; i++)//������ʷ�û�����
    {
        if( 0 == strcmp(userNick, G_myRainbowRestaurant.history_serve_users[i].userNick) )
        {
            G_myRainbowRestaurant.history_serve_users[i].moneyInAccount = money;
            G_myRainbowRestaurant.history_serve_users[i].IsVIP = money>=1000?1:0;

            //����
            writeUserInfoToFile(G_myRainbowRestaurant.history_serve_users, G_myRainbowRestaurant.historyUsersCount);
            return;
        }
    }
    printf("����ִ�����д���ģ����ڳ�������һ����������history_serve_users��user���˻����");
    rainbow_sleep(1000);
    exit(EXIT_FAILURE);

}

void deleteUserFromUsersHistory(char userNick[])//ǿ��֢ר��ɾ����
{
    //�ҵ�Ӧ��ɾ����Ԫ�ص�λ��   
    int i=0;
    while( 0!=strcmp(userNick, G_myRainbowRestaurant.history_serve_users[i].userNick) )
    {
        i++;
        if( (i+1)>G_myRainbowRestaurant.historyUsersCount )
        {
            printf("����ִ�����д���ģ����ڳ���ɾ��һ����������history_serve_users��user");
            rainbow_sleep(1000);
            exit(EXIT_FAILURE);
        }
    }
    //�Ѻ�ߵ��ڴ���ǰ�����ƶ�һ��Ԫ�صĳ��ȣ�Ȼ���������һ��λ�þͿճ����ˣ�
    struct User *dest=NULL, *src=NULL;
    dest = &G_myRainbowRestaurant.history_serve_users[i];
    src = &G_myRainbowRestaurant.history_serve_users[i+1];
    int arrLen = G_myRainbowRestaurant.historyUsersCount;
    size_t size = sizeof(struct User) * ( arrLen - (i+1) );
    memcpy(dest, src, size);

    //����һ���С�����ʵ��¿ռ䣬�Ѿɵ����ݸ��ƽ�ȥ��Ȼ��free���ɵĿռ�
    struct User *oldMemory = G_myRainbowRestaurant.history_serve_users;
    size_t lessSize = sizeof(struct User) * (arrLen - 1);
    struct User *newMemory = (struct User *)malloc(lessSize);
    if_Is_Null_Then_End_Program(newMemory);
    memcpy(newMemory, oldMemory, lessSize);
    free(oldMemory);

    //�������ݵ��ʺ�����������ݲ�����
    G_myRainbowRestaurant.history_serve_users = newMemory;
    G_myRainbowRestaurant.historyUsersCount--;
    writeUserInfoToFile(G_myRainbowRestaurant.history_serve_users, G_myRainbowRestaurant.historyUsersCount);
    
}

void loadUserDataToCurrentServeUserFromUsersHistory(char userNick[])
{
    int i;
    struct User *aHistoryUser;
    for(i=0; i<G_myRainbowRestaurant.historyUsersCount; i++)//������ʷ�û�
    {
        aHistoryUser = &G_myRainbowRestaurant.history_serve_users[i];
        if( 0 == strcmp(userNick, aHistoryUser->userNick) )
        {
            G_myRainbowRestaurant.current_serve_user = *aHistoryUser;
            break;
        }
    }
}

void updateUserInfoToUsersHistory(struct User user)
{
    int i;
    for(i=0; i<G_myRainbowRestaurant.historyUsersCount; i++)//������ʷ�û�
    {
        if( 0==strcmp(user.userNick, G_myRainbowRestaurant.history_serve_users[i].userNick) )
        {
            G_myRainbowRestaurant.history_serve_users[i] = user;
            return;
        }
    }
    //��Ӧ�����е��±���Щ����ģ����������������Ƶģ������Ҳ������
    printf("Error: ����� struct User ���� history_serve_users ��!\n");
    printf("��ȥ Debug !.");//
    exit(EXIT_FAILURE);
}

void updateUserInfoToHistoryAndFile(struct User user)
{
    if( isThisUserInUsersHistory(user.userNick) )//�������û������û�
    {
        //���¸��û���ʷ�û�����
        updateUserInfoToUsersHistory(G_myRainbowRestaurant.current_serve_user);
    }
    else
    {
        //��Ӹ��û�����ʷ�û�����
        addUserToHistory(user);
    }
    writeUserInfoToFile(G_myRainbowRestaurant.history_serve_users, G_myRainbowRestaurant.historyUsersCount);
}

void printPopularFood(int maxOutNum)
{
    int num = G_myRainbowRestaurant.foodsCount;
    maxOutNum = maxOutNum<num?maxOutNum:num;//ȡС���Ǹ�����Ȼ����Խ����
    struct food *foods;
    //foods = (struct food *)malloc(num*sizeof(struct food));
    //��ñ����˲�������ʳ����Ϣ��һ���ṹ������
    foods = convertLinkTableToFoodInfo(G_myRainbowRestaurant.foods_we_have, num);
    struct food tempFood;
    int i, j;
    //����ʳ�ﱻ������Ӵ�С����
    for(i=0; i<num-1; i++)
    {
        for(j=i+1; j<num; j++)
        {
            if( !(foods[i].orderCount >= foods[j].orderCount) )
            {
                tempFood = foods[i];
                foods[i] = foods[j];
                foods[j] = tempFood;
            }
        }
    }
    printf("���\t����\t\t�������\n");
    printf("---------------------------------------\n");
    for(i=0; i<maxOutNum; i++)//���ǰmaxOutNum������
    {
        printf("%d\t%-12s\t%d\n", foods[i].id, foods[i].name, foods[i].orderCount);
    }
    printf("---------------------------------------\n");
}

void printRichUser(int maxOutNum)
{
    int num = G_myRainbowRestaurant.historyUsersCount;
    maxOutNum = maxOutNum<num?maxOutNum:num;//ȡС���Ǹ�����Ȼ����Խ����

    //��ñ����˲��������û���Ϣ��һ���µĽṹ�����飨���ܸ�ԭ����Ŷ��
    struct User *users;
    users = (struct User *)malloc(num*sizeof(struct User));
    if_Is_Null_Then_End_Program(users);
    memcpy(users, G_myRainbowRestaurant.history_serve_users, num*sizeof(struct User));
    
    struct User tempUser;
    int i, j;
    //�����û��˻����Ӵ�С����
    for(i=0; i<num-1; i++)
    {
        for(j=i+1; j<num; j++)
        {
            if( !(users[i].moneyInAccount > users[j].moneyInAccount) )
            {
                tempUser = users[i];
                users[i] = users[j];
                users[j] = tempUser;
            }
        }
    }
    printf("---------------------------------------\n");
    for(i=0; i<maxOutNum; i++)//���ǰmaxOutNum������
    {
        printf("�û���: %s\t�˻����: %d\n", users[i].userNick, users[i].moneyInAccount);
    }
    printf("---------------------------------------\n");
}

void rainbow_sleep(unsigned long dwMilliseconds)
{
    //window.h�� typedef unsigned long DWORD ��
    //��������ʵ������һ������
    Sleep((DWORD)dwMilliseconds);
}

/*
int rainbow_printf(const char *__restrict__ _Format, ...)
{
    //do some other things
    return printf(_Format, __VA_ARGS__);
}
*/

void if_Is_Null_Then_End_Program(void *ptr)
{
    if(  ptr==NULL )
    {
        printf("���������� !\n���ڴ����ʧ��\n���������ϵͳ�ڴ治�㵼�µ�\n�����˳�����...\n");
        rainbow_sleep(1000);
        exit(EXIT_FAILURE);
    }
}