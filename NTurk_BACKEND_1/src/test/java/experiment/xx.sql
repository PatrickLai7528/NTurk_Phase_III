-- Q1
select cid
from customers
where cid not in (
   select c.cid
   from customer c, agents a
   where a.city = "New York" and (c.cid, a.aid) in (
      select o.cid, o.aid from orders
   )
)

select cid, cname
from customers
where cid not in (
   select cid
   from orders
   where aid in (
      select aid
      from agents
      where city = 'New York'
   )
)

select cid, cname
from customers
where not exists(
  select * from agent a
  where a.city = 'New York'
    and exists (
      select * from orders o
      where o.cid = c.cid and o.aid = a.aid
    )
)


-- Q2
select cid, cname
from customers
where cid in (
   select cid
   from orders o1
   where o1.oid in (
      select oid
      from orders o2
      where o1.cid = o2.cid and o1.pid <> o2.pid and oid not in
   )
)
-- 这个不行

select o1.oid
from orders o1, orders o2, orders o3
where o1.cid = o2.cid and o2.cid = o3.cid
  and o1.pid <> o2.pid and o1.pid <> o3.pid and o2.pid <> o3.pid

select oid

in 两种 not in 三种

select cid, cname
from customers
where cid in (
  select cid
  from orders
  group by cid
  having count(distinct pid) = 2
)

-- 第三题 错的
select cid, cname
from customers
where cid not in (
   select cid
   from orders
   where dols < 1000
--    where pid in (
--       select pid
--       from products
--       where p.price < 1000
--    )
)

-- 第四题
where cid not in (
   select cid from orders
   where not exists (
      select * from products
      where price > 1 and pid not in (
         select pid from orders
         where cid = oid and aid in (在纽约的aid)
      )
   )
)

-- Q5 可以改写成 not in 吗？呃什么意思
where cid not exists (
   select
)

select cid
from orders
group by cid
having count(pid) = 1

-- Q6 怎么在group调count
select cid, cname
from customers c
-- group by cid
-- having count(select * from ) = 1
where count(
  select cid from orders
  where pid = x and cid = c.cid
) = 1

-- Q7
select cid, cname
from customers c
where cid in (
  select cid from orders o
  where pid = x
) and cid not in (
  select cid from orders 0
  where pid <> x
)

-- Q8 不存在一个城市，这个商品没通过这个城市的供应商销售过（不是，这句的意思卖给这个城市的客户）
select o1 pid
from orders o1
where not exists (
  select cs.city from customers cs
  where not exists (
    select * from orders o2
    where o1.pid = o2.pid and aid in (
      select aid from agents
      where city = cs.city
    )
  )
)

-- Q9 感觉不靠谱，加果有时间相同的是任意
select cid, o1.orddate, o2.orddate
from orders o1, orders o2
where o1.cid = o2.cid and o1.orddate = all (
  select max(orddate) -- 最近的
  from orders o
  where o.cid = o1.cid
) and o2.orddate = all (
  select max(orddate) -- 第二近的
  from orders o2
  where o2.cid = cs.cid and orddate <> some ( -- 这个用some是担心有两份同时的最近
    select max(orddate)
    from orders o3
    where o3.cid = cs.cid
  )
)


-- Q10
select a.aid, a.aname
from agents a
where not exists (
  select * from customers cs
  where cs.discnt = all (select max(discnt) from customers) and not exists(
    select * from orders o
    where a.aid = o.aid and cs.cid = o.cid
  )
)


-- 第二波
-- 1
select cname, dept
from Course
where cname like '数据库' -- 怎么写啊，还有下面的

-- 2
select cno, cname
from Course
where dept is null

-- 3
select ino, iname
from instructor
where ino not in (
   select ino from Teach
   where 2014 <= semester and semester =< 2016  -- 数字写前面这是可以的吧？
)

-- 4
select i.ino, i.iname
from instructors i
where not exists (
  select * from Teach t
  where t.ino = i.ino and cno not in(
    select cs.cno from Course cs
    where cs.dept = i.dept
  )
)

-- 5
select s.sno, s.sname
from Student s
where