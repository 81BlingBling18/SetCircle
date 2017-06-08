# SetCircle
*** 
软件工程上机实验，包括四个问题，现已经完成两个
### Q1
In a box bounded by [-1, 1], given m balloons(they cannot overlap) with variable radio r and position mu, find the optimal value of r and mu which maximize sum r^2

Solution: 

将正方形面积等分成100*100个小方格，用一个链表记录小方格的交点，一个链表记录已经找出的圆。每次遍历这些交点，找出现有条件下的最大圆，将其加入已经找出的圆的链表。同时将圆内的点从交点链表中删除。直到找出所给数目的圆为止。

那么如何找出现有条件下的最大圆呢。最大圆一定是与圆或者是四条边想切的，因此只需求出当前点与四条边、所有已经找出的圆的距离，取最小值即可。

### Q2
In a box bounded by [-1, 1], given m balloons(they cannot overlap) with variable radio r and position mu. And some tiny blocks are in the box at given position d;balloons cannot overlap with these blocks. find the optimal value of r and mu which maximizes sum $r^2

Solution:

相比较第一题来说，本题中增加了一些不可覆盖的点，那么我们可以将这些点作为半径为0的圆存入已经找出的圆链表中。在绘图时将这些圆删除即可。


