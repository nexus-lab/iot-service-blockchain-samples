const fs = require('fs');
const readline = require('readline');

function findSeparators(line) {
  const separators = [];
  for (let i = 0; i < line.length; i++) {
    if (line[i] === '+') {
      separators.push(i);
    }
  }
  return separators;
}

function determineTableType(table) {
  if (table.data && table.data.length > 0) {
    if (table.data[0][0] === 'Metric') {
      return 'resource';
    }
    if (table.data[0].indexOf('Throughput (TPS)') > -1) {
      return 'performance';
    }
  }
  return 'unknown';
}

function transformTable(table) {
  const processed = [];
  const type = determineTableType(table);

  if (type === 'resource') {
    const metricNames = [
      'Avg Memory (MB)',
      'CPU (%)',
      'Network In (MB)',
      'Network Out (MB)',
      'Disc Write (MB)',
      'Disc Read (MB)',
    ];
    const hosts = (table.data.length - 1) / metricNames.length;
    for (let i = 0; i < hosts; i++) {
      processed.push([table.data[i + 1][2]]);
    }
    for (let i = 0; i < table.data.length - 1; i++) {
      processed[i % hosts].push(table.data[i + 1][3]);
    }
    processed.unshift(['Host', ...metricNames]);
  } else if (type === 'performance') {
    processed.push([
      'Transactions',
      'Send Rate (tps)',
      'Max Latency (s)',
      'Min Latency (s)',
      'Avg Latency (s)',
      'Throughput (tps)',
    ]);
    processed.push([
      table.data[1][1],
      table.data[1][3],
      table.data[1][4],
      table.data[1][5],
      table.data[1][6],
      table.data[1][7],
    ]);
  }

  return { type, data: processed };
}

/* eslint-disable no-console */
function printCsvTable(table) {
  table.data.forEach((row) => {
    console.log(row.join(','));
  });
}

function parse(filepath) {
  const reader = readline.createInterface({
    input: fs.createReadStream(filepath),
    terminal: false,
  });

  let begin = false;
  let table = {};
  let separators = [];

  reader.on('line', (line) => {
    if (line.startsWith('+--')) {
      begin = !begin;
      if (begin) {
        separators = findSeparators(line);
        table = { type: '', data: [] };
      } else {
        const transformedTable = transformTable(table);
        printCsvTable(transformedTable);

        table = {};
        separators = [];
      }
    } else if (begin && line.startsWith('| ')) {
      const values = [];
      separators.reduce((prev, curr) => {
        if (curr > 0) {
          values.push(line.substring(prev + 1, curr).trim());
        }
        return curr;
      });
      table.data.push(values);
    } else if (!begin || !line.startsWith('|--')) {
      console.log(line);
    }
  });
}

if (process.argv.length !== 3) {
  console.error('Usage: node ./result_to_csv.js <file>');
} else {
  parse(process.argv[2]);
}
