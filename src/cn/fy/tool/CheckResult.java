package cn.fy.tool;

public class CheckResult {

    //判断胜负,参数board是棋盘下子的数组，curX,curY分别是当前下子的坐标
    public static boolean checkWin(int[][] board, int curX, int curY, int type) {
        final int WINCOUNT = 5;
        int same = 0;//相同的子数
        int nearX, nearY;//与当前子相邻的子的坐标
        //判断横向是否胜利
        nearX = curX;
        nearY = curY;
        while (--nearX > -1) {
            if (board[nearX][nearY] == type && same < WINCOUNT - 1)
                same++;
            else break;
        }
        if (same == WINCOUNT - 1) return true;
        if (WINCOUNT - same - 1 < 15 - curX) {//向后判断
            nearX = curX;
            int t = curX + WINCOUNT - same > 15 ? 15 : curX + WINCOUNT - same;
            while (++nearX < t && same < WINCOUNT - 1) {
                if (board[nearX][nearY] == type)
                    same++;
                else break;
            }
        }
        if (same == WINCOUNT - 1) return true;
        //判断纵向是否胜利
        same = 0;
        nearX = curX;
        while (--nearY > -1) {
            if (board[nearX][nearY] == type && same < WINCOUNT - 1)
                same++;
            else break;
        }
        if (same == WINCOUNT - 1) return true;
        if (WINCOUNT - same - 1 < 15 - curY) {//向后判断
            nearY = curY;
            int t = curY + WINCOUNT - same > 15 ? 15 : curY + WINCOUNT - same;
            while (++nearY < t && same < WINCOUNT - 1) {
                if (board[nearX][nearY] == type)
                    same++;
                else break;
            }
        }
        if (same == WINCOUNT - 1) return true;
        //判断正斜向是否胜利
        same = 0;
        nearX = curX;
        nearY = curY;
        while (--nearX > -1 && --nearY > -1) {
            if (board[nearX][nearY] == type && same < WINCOUNT - 1)
                same++;
            else break;
        }
        if (same == WINCOUNT - 1) return true;
        if (WINCOUNT - same - 1 < 15 - curX && WINCOUNT - same - 1 < 15 - curY) {//向后判断
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
        //判断反斜向是否胜利
        same = 0;
        nearX = curX;
        nearY = curY;
        while (++nearX < 15 && --nearY > -1 && same < WINCOUNT - 1) {
            if (board[nearX][nearY] == type)
                same++;
            else break;
        }
        if (same == WINCOUNT - 1) return true;
        if (WINCOUNT - same - 1 < curX && WINCOUNT - same - 1 < 15 - curY) {//向后判断
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

    //把秒数转化为分钟数和秒数的形式
    public static String mAnds(int time) {
        int h = time / 3600;
        int m = (time - h * 3600) / 60;
        int s = time - h * 3600 - m * 60;
        return h + " : " + m + " : " + s;
    }
}
