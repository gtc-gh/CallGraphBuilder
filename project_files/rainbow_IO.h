//���ļ���ȡʳ����Ϣ
void readFoodInfoFromFile(struct food *(*foods), int *num);
//��ʳ����Ϣд���ļ�
void writeFoodInfoToFile(struct food foods[], int num);

//���ļ���ȡ�û���Ϣ
void readUserInfoFromFile(struct User *(*users), int *num);
//���ļ�д��ʳ����Ϣ
void writeUserInfoToFile(struct User user[], int num);

//���ļ���ȡ����Ա����
char *readSuperAdminPasswordFromFile(void);
//���ļ�д�����Ա����
void writeSuperAdminPasswordToFile(char password[]);

//���ļ���ȡ��λͼ��Ϣ
void readSeatMapInfoFromFile(struct Seat *pSeatsMap);
//���ļ�д����λͼ��Ϣ
void writeSeatMapInfoToFile(struct Seat *pSeatsMap);

//����ȫ�µ����ݿ��ļ���ԭ�ȵ����ݽ������
void createFreshDataBaseFiles(void);
