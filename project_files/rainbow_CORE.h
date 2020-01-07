//�û��ǳ� ��󳤶�
#define USERNICK_LENGTH_MAX 30
//ʳ������ ��󳤶�
#define FOODNAME_LENGTH_MAX 30
//����Ա���� ��󳤶�
#define ADMINPAS_LENGTH_MAX 30
//��λ���� ����
#define SEATINFO_WIDTH 9
//��λ���� ����
#define SEATINFO_HEIGHT 7

//����
struct Date
{
    int year;
    int month;
    int day;
};
//ʱ��
struct Time
{
    int hour;
    int minute;
    int second;
};
//��Ʒ
struct food
{
    int id;//ʳ��ID(Ψһ��)
    char name[FOODNAME_LENGTH_MAX];//ʳ������
    int price;//ʳ��۸�
    int orderCount;//����˴���ӵ�ϵͳ������һ������Ĵ���
};
//����
struct Order
{
    //struct Date date;
    //struct Time time;
    //long orderID;
    int foodCount;//��ǰ������ʳ����
    struct food *foods;
    int ExpensesToBePaid;//��ǰ����Ӧ������
};
//�û�����
enum UserType
{
    undefine,//����֪����δ��¼��
    customer,//�˿�
    admin//����Ա
};
//��λ
struct Seat
{
    int isSelected;//�����λ�Ƿ�����
    int row;//��λ�к�
    int col;//��λ�к�
};
//�û�
struct User
{
    //long userID;//TODO:
    char userNick[USERNICK_LENGTH_MAX];
    //struct Date registerDate;//TODO:
    int IsVIP;//�Ƿ�VIP
    int moneyInAccount;//�û��˻����
    //��ֻŪһ������
    struct Order order;//��ǰ����
    enum UserType userType;//�û�����
    struct Seat seat;//�û���λ
};

//������־
struct RestaurantDailyLog
{
    struct Date date;
    int orderCount;
    struct Order *orders;
};

//����ڵ�(ʳ��)
struct node
{
    struct food data;
    struct node *next;
};
typedef struct node NODE;

//�ʺ������������
struct RainbowRestaurant
{
    NODE *foods_we_have;//ָ�� ָ��ǰ����ӵ�е����в�Ʒ�Ľṹ������Ŀռ�
    int foodsCount;//��ǰ����ӵ�еĲ�Ʒ����
    struct User *history_serve_users;//ָ�� ָ�������������������û����û���Ϣ�ṹ������Ŀռ�
    int historyUsersCount;
    //�ȿ���ֻ������һ���˿ͣ���Ȼ��������
    struct User current_serve_user;//��ǰ������û�
    char superAdmin_password[ADMINPAS_LENGTH_MAX + 1];//ϵͳ����Ա����
    struct Seat seatsMap[SEATINFO_HEIGHT][SEATINFO_WIDTH];//������λͼ��Ϣ

} G_myRainbowRestaurant;


//���һ��ʳ�����ݵ�һ������β��
NODE *addNodeToTail(NODE *pHead, struct food data);
//���һ��ʳ�����ݵ�һ������ͷ��
NODE *addNodeToHead(NODE *pHead, struct food data);
//���һ��ʳ�����ݵ�һ���������� ����ʳ��ID��������
NODE *addNodeByOrder(NODE *pHead, struct food data);
//ɾ�����������һ���ڵ� ���ݴ����ʳ������
NODE *deleteNodeByValue(NODE *pHead, struct food data);
//ɾ�����������һ���ڵ� ���ݴ����ʳ�����ݵ�ID
NODE *deleteRestaurantFoodByID(NODE *pHead, int id);
//�жϴ��������ʳ��ṹ���Ƿ����
int isTwoFoodEqual(struct food food1, struct food food2);
//ת��ʳ���������鵽����(ʳ��)
NODE *loadFoodInfoToLinkTable(struct food *foods, int num);
//ת������(ʳ��)��ʳ����������
struct food *convertLinkTableToFoodInfo(NODE *pHead, int num);
//����ǰ������ʳ��������Ϣ����
void saveFoodInfo(void);
//��ӡָ������
void printLinkTable(NODE *pHead);


//��ʼ������ ������ȡ�����ļ���ȫ�ֱ�����ʼ��
int rainbow_init(int argc, char const *argv[]);
//��һ��ʳ��������ӵ�ָ������
int addFoodToOrder(int foodID, struct Order *order);
//�ѵ�ǰ�û�������ʷ�û��б�
void addUserToHistory(struct User user);
//��һ��ʳ��������ӵ�������ʳ����������
void addFoodToRestaurant(struct food *newFood);
//��ȡһ���µĿ���foodID
int getAvailableFoodID(void);

//��ӡ��ǰ��������λ��Ϣͼ
void printSeatsMap(void);

//�����ǳ��жϲ���֮ǰ�Ƿ���������˿ͣ����ж�����û��Ƿ�����ʷ�����У�
int isThisUserInUsersHistory(char userNick[]);

//�����û����趨ָ���û����˻����
void setUserMoneyFromUsersHistory(char userNick[], int Money);

//�����û�������ʷ�û�������ɾ���û�
void deleteUserFromUsersHistory(char userNick[]);

//����ʷ�û������в������û�������Ϊ��ǰ���ڵķ����û�����ȷ�����û���������ʷ�����У���
void loadUserDataToCurrentServeUserFromUsersHistory(char userNick[]);

//�������struct User���µ���Ӧ�û�������ʷ�û����ݣ���ȷ�����û���������ʷ�����У���
void updateUserInfoToUsersHistory(struct User user);

//�������struct User���»���ӵ���Ӧ�û�������ʷ�û����ݲ�����
void updateUserInfoToHistoryAndFile(struct User user);

//��ӡǰn�����ܻ�ӭ��ʳ��
void printPopularFood(int maxOutNum);
//��ӡǰn������Ǯ���û�?
void printRichUser(int maxOutNum);

//������ָͣ������ʱ��
void rainbow_sleep(unsigned long dwMilliseconds);

//��������ָ����NULL��ô�ͱ�����������
void if_Is_Null_Then_End_Program(void *ptr);

