/*Get all songs uploaded by a specific content manager*/
SELECT s.title, s.author, s.upload_date, s.download_price
FROM Songs s
JOIN Users u ON s.content_manager_id = u.user_id
WHERE u.username = 'lebogang';

/*Top streamed songs (by number of streams)*/
SELECT s.title, COUNT(st.stream_id) AS stream_count
FROM Songs s
JOIN Streams st ON s.song_id = st.song_id
GROUP BY s.title
ORDER BY stream_count DESC
LIMIT 5;

/*Total earnings per content manager (grouped)*/
SELECT u.username, SUM(e.amount) AS total_earnings
FROM Earnings e
JOIN Users u ON e.content_manager_id = u.user_id
GROUP BY u.username;

/*Consumers and their downloaded songs*/
SELECT u.username, s.title, d.download_date, d.credit_cost
FROM Downloads d
JOIN Users u ON d.user_id = u.user_id
JOIN Songs s ON d.song_id = s.song_id
WHERE u.role = 'consumer';


/*All active subscriptions*/
SELECT u.username, s.start_date, s.end_date, s.type
FROM Subscriptions s
JOIN Users u ON s.user_id = u.user_id
WHERE CURRENT_DATE BETWEEN s.start_date AND s.end_date;

/*Total credits purchased by each user*/
SELECT u.username, SUM(c.amount) AS total_credits
FROM Credits c
JOIN Users u ON c.user_id = u.user_id
GROUP BY u.username;

/*Songs and how many times they were downloaded*/
SELECT s.title, COUNT(d.download_id) AS total_downloads
FROM Songs s
LEFT JOIN Downloads d ON s.song_id = d.song_id
GROUP BY s.title
ORDER BY total_downloads DESC;

/*Show unpaid earnings for each content manager*/
SELECT u.username, e.amount, e.period_start, e.period_end
FROM Earnings e
JOIN Users u ON e.content_manager_id = u.user_id
WHERE e.status = 'unpaid';

/*Monthly earnings report (for July 2025)*/
SELECT u.username, SUM(e.amount) AS july_earnings
FROM Earnings e
JOIN Users u ON e.content_manager_id = u.user_id
WHERE e.period_start >= '2025-07-01' AND e.period_end <= '2025-07-31'
GROUP BY u.username;

/*Consumers who have never streamed a song*/
SELECT u.username
FROM Users u
LEFT JOIN Streams s ON u.user_id = s.user_id
WHERE u.role = 'consumer' AND s.stream_id IS NULL;

