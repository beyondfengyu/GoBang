package cn.fy.tool;

public class CheckResult {

    //�ж�ʤ��,����board���������ӵ����飬curX,curY�ֱ��ǵ�ǰ���ӵ�����
    public static boolean checkWin(int[][] board, int curX, int curY, int type) {
        final int WINCOUNT = 5;
        int same = 0;//��ͬ������
        int nearX, nearY;//�뵱ǰ�����ڵ��ӵ�����
        //�жϺ����Ƿ�ʤ��
        nearX = curX;
        nearY = curY;
        while (--nearX > -1) {
            if (board[nearX][nearY] == type && same < WINCOUNT - 1)
                same++;
            else break;
        }
        if (same == WINCOUNT - 1) return true;
        if (WINCOUNT - same - 1 < 15 - curX) {//����ж�
            nearX = curX;
            int t = curX + WINCOUNT - same > 15 ? 15 : curX + WINCOUNT - same;
            while (++nearX < t && same < WINCOUNT - 1) {
                if (board[nearX][nearY] == type)
                    same++;
                else break;
            }
        }
        if (same == WINCOUNT - 1) return true;
        //�ж������Ƿ�ʤ��
        same = 0;
        nearX = curX;
        while (--nearY > -1) {
            if (board[nearX][nearY] == type && same < WINCOUNT - 1)
                same++;
            else break;
        }
        if (same == WINCOUNT - 1) return true;
        if (WINCOUNT - same - 1 < 15 - curY) {//����ж�
            nearY = curY;
            int t = curY + WINCOUNT - same > 15 ? 15 : curY + WINCOUNT - same;
            while (++nearY < t && same < WINCOUNT - 1) {
                if (board[nearX][nearY] == type)
                    same++;
                else break;
            }
        }
        if (same == WINCOUNT - 1) return true;
        //�ж���б���Ƿ�ʤ��
        same = 0;
        nearX = curX;
        nearY = curY;
        while (--nearX > -1 && --nearY > -1) {
            if (board[nearX][nearY] == type && same < WINCOUNT - 1)
                same++;
            else break;
        }
        if (same == WINCOUNT - 1) return true;
        if (WINCOUNT - same - 1 < 15 - curX && WINCOUNT - same - 1 < 15 - curY) {//����ж�
            nearX = curX;
            nearY = curY;
            int s = curX + WINCOUNT - same > 15 ? 15 : curX + WINCOUNT - same;
            int e = curY + WINCOUNT - same > 15 ? 15 : curY + WINCOUNT - same;
            while (++nearX < s && ++nearY < e && same < WINCOUNT - 1) {
                if (board[nearX][nearY] == type)
                    same++;
                else break;
            }
        }
        if (same == WINCOUNT - 1) return true;
        //�жϷ�б���Ƿ�ʤ��
        same = 0;
        nearX = curX;
        nearY = curY;
        while (++nearX < 15 && --nearY > -1 && same < WINCOUNT - 1) {
            if (board[nearX][nearY] == type)
                same++;
            else break;
        }
        if (same == WINCOUNT - 1) return true;
        if (WINCOUNT - same - 1 < curX && WINCOUNT - same - 1 < 15 - curY) {//����ж�
            nearX = curX;
            nearY = curY;
            int e = curY + WINCOUNT - same > 15 ? 15 : curY + WINCOUNT - same;
            while (--nearX > -1 && ++nearY < e && same < WINCOUNT - 1) {
                if (board[nearX][nearY] == type)
                    same++;
                else break;
            }
        }
        if (same == WINCOUNT - 1) return true;
        return false;
    }

    //������ת��Ϊ����������������ʽ
    public static String mAnds(int time) {
        int h = time / 3600;
        int m = (time - h * 3600) / 60;
        int s = time - h * 3600 - m * 60;
        return h + " : " + m + " : " + s;
    }
}
