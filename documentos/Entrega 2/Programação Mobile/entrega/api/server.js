const express = require('express');
const mysql = require('mysql2/promise');
const bcrypt = require('bcryptjs');

const app = express();
app.use(express.json());

const pool = mysql.createPool({
  host: 'localhost',
  user: 'appuser',
  password: 'appuser',
  database: 'meuapp'
});

app.post('/registrar', async (req, res) => {
  try {
    const { nome, email, senha } = req.body;
    if (!nome || !email || !senha) return res.sendStatus(400);
    const hash = await bcrypt.hash(senha, 10);
    await pool.query('INSERT INTO contas (nome, email, senha_hash) VALUES (?, ?, ?)', [nome, email, hash]);
    res.sendStatus(200);
  } catch (e) {
    if (e.code === 'ER_DUP_ENTRY') return res.sendStatus(409);
    console.error(e);
    res.sendStatus(500);
  }
});

app.post('/login', async (req, res) => {
  try {
    const { email, senha } = req.body;
    if (!email || !senha) return res.sendStatus(400);
    const [rows] = await pool.query('SELECT nome, senha_hash FROM contas WHERE email = ?', [email]);
    if (rows.length === 0) return res.sendStatus(401);
    const ok = await bcrypt.compare(senha, rows[0].senha_hash);
    if (!ok) return res.sendStatus(401);
    res.json({ nome: rows[0].nome });
  } catch (e) {
    console.error(e);
    res.sendStatus(500);
  }
});

app.listen(3000, '0.0.0.0', () => console.log('API rodando na porta 3000'));
