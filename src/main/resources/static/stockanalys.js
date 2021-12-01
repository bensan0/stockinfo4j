// function fetchApiPost() {
//     let req = new Request('http://127.0.0.1:8080/test/posthtml?key1=V1&key2=V2', { method: 'POST', body: '{"key1": "V1"}' });

//     fetch(req)
//         .then(res => {
//             if (res.status == 200) {
//                 return res.json();
//             } else {
//                 throw new Error(res.statusText);
//             }

//         })
//         .then(res => {
//             console.log(res)
//         })
//         // catch any error in the network call.
//         .catch(error => {
//             console.error(error);
//         });

// }

// function fetchApiGet() {
//     let req = new Request('http://127.0.0.1:8080/financial2/industry/industry');

//     fetch(req)
//         .then(res => {
//             if (res.status == 200) {
//                 return res.json();
//             } else {
//                 throw new Error(res.statusText);
//             }
//         })
//         .then(industryObj => {
//             console.log(industryObj);
//             console.log(industryObj['error'] === null)
//             let indust = document.getElementById('indust');
//             industryObj['data'].forEach(function (data, i) {
//                 indust.innerHTML += '<tr>' +
//                     '<th scope="row">' + (i + 4) + '</th>' +
//                     '<td colspan="2">' + data['Comment'] + '</td>' +
//                     '<td>' + data['Id'] + '</td>' +
//                     '</tr>'
//             });
//         })
//         // catch any error in the network call.
//         .catch(error => {
//             console.error(error);
//         });

// }

function getDaily() {
    clearDailyStatus();
    let date = document.getElementById('getdailyyyyymmdd').value;
    console.log(date)
    let req = new Request('http://127.0.0.1:8081/stockinfo4j/downloaders/daily?date=' + date);
    fetch(req)
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            console.log(jsonObj)
            let err = jsonObj['errorMsg']
            if (err['code'] === '0000') {
                document.getElementById('getdailystatus').innerHTML = 'OK';
            } else {
                document.getElementById('getdailystatus').innerHTML = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('getdailystatus').innerHTML = error;
        });
}

function getDistribution() {
    clearGetDistributionStatus();
    let req = new Request('http://127.0.0.1:8081/stockinfo4j/downloaders/distribution');
    fetch(req)
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            console.log(jsonObj)
            let err = jsonObj['errorMsg']
            if (err['code'] === '0000') {
                document.getElementById('getdistributionstatus').innerHTML = 'OK';
            } else {
                document.getElementById('getdistributionstatus').innerHTML = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('getdistributionstatus').innerHTML = error;
        });
}

function filtTradingvol() {
    clearFilter();
    console.log('filterdate:' + document.getElementById('filterdate').value + ';' + document.getElementById('filterdate').innerText + ';' + document.getElementById('filterdate').innerHTML)
    let req = new Request('http://127.0.0.1:8081/stockinfo4j/search/filtstockdaily',
        {
            method: 'POST',
            body: (JSON.stringify({
                date: document.getElementById('filterdate').value,
                tradingVolFlucPercentLL: document.getElementById('filterlowerpercent').value,
                tradingVolFlucPercentUL:document.getElementById('filterhigherpercent').value,
                yesterdayTradingVolLL:document.getElementById('filterlowervol').value,
                yesterdayTradingVolUL:document.getElementById('filterhighervol').value,
                todayClosingUL:document.getElementById('filterhigherprice').value
            })),
            headers: {'Content-Type': 'application/json; charset=utf-8'}
        }
    )
    fetch(req)
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            console.log(jsonObj);
            let err = jsonObj['errorMsg']
            if (err['code']==="0000") {
                document.getElementById('filterstatus').innerHTML = 'OK';
                let filter = document.getElementById('filtertbody');
                let filtertranstbody = document.getElementById('filtertranstbody');
                jsonObj['data'].forEach(function (data) {
                    filter.innerHTML += '<tr>' +
                        '<th scope="row">' + data['code'] + '</th>' +
                        '<td>' + data['name'] + '</td>' +
                        '<td>' + data['industry'] + '</td>' +
                        '</tr>';

                    data['tranList'].forEach(function (datatrans) {
                        filtertranstbody.innerHTML += '<tr>' +
                            '<th scope="row">' + datatrans['code'] + '</th>' +
                            '<td>' + datatrans['name'] + '</td>' +
                            '<td>' + datatrans['tradingVol'] + '</td>' +
                            '<td>' + datatrans['closing'] + '</td>' +
                            '<td>' + datatrans['flucPer'] + '</td>' +
                            '<td>' + datatrans['fluc'] + '</td>' +
                            '<td>' + datatrans['date'] + '</td>' +
                            '</tr>';
                    });
                });
            } else {
                document.getElementById('filterstatus').innerHTML = err + '\n' + jsonObj['errorMsg'];
            }
        })
        .catch(error => {
            document.getElementById('filterstatus').innerHTML = error;
        });
}

function getStockTrans() {
    clearStockTrans();
    let days = document.getElementById('stocktransdays').value
    let code = document.getElementById('stocktranscode').value
    getStockOtherInfo(code)
    let req = new Request('http://127.0.0.1:8081/stockinfo4j/search/stocktran?' + 'days=' + days + '&code=' + code);

    fetch(req)
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            console.log(jsonObj);
            let err = jsonObj['errorMsg']
            if (err['code']==='0000') {
                document.getElementById('stocktransstatus').innerHTML = 'OK';
                let stocktranstbody = document.getElementById('stocktranstbody');
                jsonObj['data'].forEach(function (data) {
                    if(data===null){
                        return
                    }
                    stocktranstbody.innerHTML += '<tr>' +
                        '<th scope="row">' + data['code'] + '</th>' +
                        '<td>' + data['name'] + '</td>' +
                        '<td>' + data['tradingVol'] + '</td>' +
                        '<td>' + data['closing'] + '</td>' +
                        '<td>' + data['flucPer'] + '</td>' +
                        '<td>' + data['fluc'] + '</td>' +
                        '<td>' + data['date'] + '</td>' +
                        '</tr>';
                });
            } else {
                document.getElementById('stocktransstatus').innerText = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('stocktransstatus').innerText = error;
        });
}

function getCorpTrans() {
    clearCorpTrans();
    let days = document.getElementById('corptransdays').value
    let code = document.getElementById('corptranscode').value

    let req = new Request('http://127.0.0.1:8081/stockinfo4j/search/corptran?' + 'code=' + code + '&days=' + days);

    fetch(req)
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            let fiTotal = 0, fcTotal = 0, iTTotal = 0, dTotal = 0, dSTotal = 0, dHTotal = 0, tTotal = 0;
            let err = jsonObj['errorMsg']
            if (err['code']==='0000') {
                document.getElementById('corptransstatus').innerHTML = 'OK';
                const corptranstbody = document.getElementById('corptranstbody');
                jsonObj['data'].forEach(function (data) {
                    corptranstbody.innerHTML += '<tr>' +
                        '<th scope="row">' + data['code'] + '</th>' +
                        '<td>' + data['name'] + '</td>' +
                        '<td>' + data['foreignInvestors'] + '</td>' +
                        // '<td>' + data['foreignCorp'] + '</td>' +
                        '<td>' + data['investmentTrust'] + '</td>' +
                        '<td>' + data['dealer'] + '</td>' +
                        // '<td>' + data['dealerSelf'] + '</td>' +
                        // '<td>' + data['dealerHedge'] + '</td>' +
                        '<td>' + data['corpTotal'] + '</td>' +
                        '<td>' + data['date'] + '</td>' +
                        '</tr>';
                    fiTotal += data['foreignInvestors'];
                    // fcTotal += data['foreignCorp'];
                    iTTotal += data['investmentTrust'];
                    dTotal += data['dealer'];
                    // dSTotal += data['dealerSelf'];
                    // dHTotal += data['dealerHedge'];
                    tTotal += data['corpTotal'];
                });
                corptranstbody.innerHTML += '<tr>' +
                    '<th scope="row" colspan="2">合計</th>' +
                    '<td>' + fiTotal + '</td>' +
                    // '<td>' + fcTotal + '</td>' +
                    '<td>' + iTTotal + '</td>' +
                    '<td>' + dTotal + '</td>' +
                    // '<td>' + dSTotal + '</td>' +
                    // '<td>' + dHTotal + '</td>' +
                    '<td>' + tTotal + '</td>' +
                    '<td></td>' +
                    '</tr>';
            } else {
                document.getElementById('corptransstatus').innerHTML = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('corptransstatus').innerHTML = error
        })
}

function clearDailyStatus() {
    document.getElementById('getdailystatus').innerHTML = '';
}

function clearGetDistributionStatus() {
    document.getElementById('getdistributionstatus').innerHTML = '';
}

function clearFilter() {
    document.getElementById('filtertbody').innerHTML = '';
    document.getElementById('filtertranstbody').innerHTML = '';
    document.getElementById('filterstatus').innerHTML = '';
}

function clearStockTrans() {
    document.getElementById('stocktranstbody').innerHTML = '';
    document.getElementById('stocktransstatus').innerHTML = '';
}

function clearCorpTrans() {
    document.getElementById('corptranstbody').innerHTML = '';
    document.getElementById('corptransstatus').innerHTML = '';
}

function getAttention() {
    fetch('http://127.0.0.1:8081/stockinfo4j/attention/all')
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            console.log(jsonObj);
            let err = jsonObj['errorMsg']
            if (err['code']==='0000') {
                let attentionstbody = document.getElementById('attentionstbody');
                attentionstbody.innerHTML = '';
                jsonObj['data'].forEach(function (data) {
                    attentionstbody.innerHTML += '<tr>' +
                        '<th scope="row">' + data['code'] + '</th>' +
                        '<td>' + data['name'] + '</td>' +
                        '<td>' + data['industry'] + '</td>' +
                        '<td>' + data['note'] + '</td>' +
                        '<td>' + data['date'] + '</td>' +
                        '<td>' + '<button onclick="deleteAttention(\'' + data['code'] + '\')">刪除</button>' + '</td>' +
                        '</tr>';
                });
            } else {
                document.getElementById('attentionsstatus').innerHTML = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('attentionsstatus').innerHTML = error;
        });
}

function addAttention() {
    fetch('http://127.0.0.1:8081/stockinfo4j/attention/add', {
        method: 'POST',
        body: /*encodeURI(*/JSON.stringify({
            code: document.getElementById('attentionscode').value,
            note: document.getElementById('attentionscomment').value
        })/*)*/,
        headers: {
            'Content-Type': 'application/json; charset=utf-8'
        }
    })
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            console.log(jsonObj);
            let err = jsonObj['errorMsg']
            if (err['code']==='0000') {
                getAttention()
            } else {
                document.getElementById('attentionsstatus').innerHTML = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('attentionsstatus').innerHTML = error;
        });
}

function deleteAttention(code) {
    fetch('http://127.0.0.1:8081/stockinfo4j/attention/del?code=' + code)
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            let err = jsonObj['errorMsg']
            if (err['code']==='0000') {
                getAttention();
            } else {
                document.getElementById('attentionsstatus').innerHTML = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('attentionsstatus').innerHTML = error;
        });
}

function searchDistribution() {
    document.getElementById('searchdistrubutionstatus').innerHTML = '';
    let weeks = document.getElementById('distrubutionweeks').value;
    let code = document.getElementById('distrubutioncode').value;
    fetch('http://127.0.0.1:8081/stockinfo4j/search/distribution?weeks=' + weeks + '&code=' + code)
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            console.log(jsonObj);
            let err = jsonObj['errorMsg']
            if (err['code']==='0000') {
                document.getElementById('searchdistrubutionstatus').innerHTML = 'OK'
                let searchdistributiontbody = document.getElementById('searchdistributiontbody');
                searchdistributiontbody.innerHTML = '';
                jsonObj['data'].forEach(function (data) {
                    let rate15 = data['rate15']
                    let total = data['total']
                    let fourToTenPeople = parseInt(data['rate12'].split('/')[0]) + parseInt(data['rate13'].split('/')[0])+ parseInt(data['rate14'].split('/')[0])
                    let fourToTenVol = parseInt(data['rate12'].split('/')[1]) + parseInt(data['rate13'].split('/')[1])+ parseInt(data['rate14'].split('/')[1])
                    let fourToTenPer = (parseFloat(data['rate12'].split('/')[2]) + parseFloat(data['rate13'].split('/')[2])+ parseFloat(data['rate14'].split('/')[2]))
                    let underFourPeople = parseInt(total.split('/')[0]) - parseInt(rate15.split('/')[0]) - fourToTenPeople
                    let underFourVol = parseInt(total.split('/')[1]) - parseInt(rate15.split('/')[1]) - fourToTenVol
                    let underFourPer = (parseFloat(total.split('/')[2]) - parseFloat(rate15.split('/')[2]) - fourToTenPer).toString()
                    underFourPer = underFourPer.substring(0,underFourPer.indexOf('.')+3)
                    searchdistributiontbody.innerHTML += '<tr>' +
                        '<th scope="row">' + data['code'] + '</th>' +
                        '<td>' + data['name'] + '</td>' +
                        '<td>' + underFourPeople + '</td>' +
                        '<td>' + underFourVol + '</td>' +
                        '<td>' + underFourPer + '</td>' +
                        '<td>' + fourToTenPeople + '</td>' +
                        '<td>' + fourToTenVol + '</td>' +
                        '<td>' + fourToTenPer + '</td>' +
                        '<td>' + rate15.split('/')[0] + '</td>' +
                        '<td>' + rate15.split('/')[1] + '</td>' +
                        '<td>' + rate15.split('/')[2] + '</td>' +
                        '<td>' + total.split('/')[0] + '</td>' +
                        '<td>' + total.split('/')[1] + '</td>' +
                        '<td>' + total.split('/')[2] + '</td>' +
                        '<td>' + data['date'] + '</td>' +
                        '</tr>';
                });
            } else {
                document.getElementById('searchdistrubutionstatus').innerHTML = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('searchdistrubutionstatus').innerHTML = error;
        });
}

function clearSearchDistribution(){
    document.getElementById('searchdistrubutionstatus').innerHTML = '';
    document.getElementById('searchdistributiontbody').innerHTML = '';
}

function getStockOtherInfo(code){
    document.getElementById('searchdistrubutionstatus').innerHTML = '';
    document.getElementById('mainbusiness').innerText = ''
    document.getElementById('per').innerText = ''
    document.getElementById('financingbalance').innerText = ''
    document.getElementById('financingbalancestatus').innerText = ''
    document.getElementById('marginbalance').innerText = ''
    document.getElementById('marginbalancestatus').innerText = ''
    document.getElementById('marginfinancing').innerText = ''
    document.getElementById('marginfinancingstatus').innerText = ''
    document.getElementById('ma5').innerText = ''
    document.getElementById('ma5bias').innerText = ''
    document.getElementById('ma10').innerText = ''
    document.getElementById('ma10bias').innerText = ''
    document.getElementById('ma20').innerText = ''
    document.getElementById('revenuedate').innerText = ''
    document.getElementById('revenuemonthfluc').innerText = ''
    document.getElementById('revenueyearfluc').innerText = ''
    fetch('http://127.0.0.1:8081/stockinfo4j/soup/stockotherinfo?code=' + code)
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            console.log(jsonObj);
            let err = jsonObj['errorMsg']
            if (err['code']==='0000') {
                document.getElementById('stockotherinfostatus').innerHTML = 'OK'
                let data = jsonObj['data']
                document.getElementById('mainbusiness').innerText = data['mainBusiness']
                document.getElementById('per').innerText = data['per']
                document.getElementById('financingbalance').innerText = data['financingBalance']
                document.getElementById('financingbalancestatus').innerText = data['financingBalanceStatus']
                document.getElementById('marginbalance').innerText = data['marginBalance']
                document.getElementById('marginbalancestatus').innerText = data['marginBalanceStatus']
                document.getElementById('marginfinancing').innerText = data['marginFinancing']
                document.getElementById('marginfinancingstatus').innerText = data['marginFinancingStatus']
                document.getElementById('ma5').innerText = data['ma5']
                document.getElementById('ma5bias').innerText = data['ma5Bias']
                document.getElementById('ma10').innerText = data['ma10']
                document.getElementById('ma10bias').innerText = data['ma10Bias']
                document.getElementById('ma20').innerText = data['ma20Bias']
                document.getElementById('revenuedate').innerText = data['revenueDate']
                document.getElementById('revenuemonthfluc').innerText = data['revenueMonthFluc']
                document.getElementById('revenueyearfluc').innerText = data['revenueYearFluc']
            } else {
                document.getElementById('stockotherinfostatus').innerText = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('stockotherinfostatus').innerText = error;
        });
}

function getSlowlyincrease() {
    clearSlowlyincrease()
    let date = document.getElementById('slowlyincreasedate').value
    let flucPercentLL = document.getElementById('slowlyincreaseflucLL').value
    let flucPercentUL = document.getElementById('slowlyincreaseflucUL').value
    let days = document.getElementById('slowlyincreasedays').value
    fetch('http://127.0.0.1:8081/stockinfo4j/search/slowlyincrease?date=' + date +  '&flucPercentLL=' + flucPercentLL
     + '&flucPercentUL=' + flucPercentUL + '&days=' + days)
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            console.log(jsonObj);
            let err = jsonObj['errorMsg']
            if (err['code']==='0000') {
                document.getElementById('slowlyincreasestatus').innerText = 'OK'
                let slowlyincreasetbody = document.getElementById('slowlyincreasetbody');
                jsonObj['data'].forEach(function (data) {
                    slowlyincreasetbody.innerHTML += '<tr>' +
                        '<th scope="row">' + data['code'] + '</th>' +
                        '<td>' + data['name'] + '</td>' +
                        '<td>' + data['industry'] + '</td>' +
                        '<td>' + data['pastPrice'] + '</td>' +
                        '<td>' + data['nowPrice'] + '</td>' +
                        '<td>' + data['flucPercent'] + '</td>' +
                        '</tr>';
                });
            } else {
                document.getElementById('slowlyincreasestatus').innerHTML = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('slowlyincreasestatus').innerHTML = error;
        });
}

function clearSlowlyincrease(){
    document.getElementById('slowlyincreasestatus').innerText = '';
    document.getElementById('slowlyincreasetbody').innerHTML = '';
}

function getSlowlyincreaseTradingVol() {
    clearSlowlyincreaseTradingVol()
    let date = document.getElementById('slowlyincreaseTradingVoldate').value
    let flucPercentLL = document.getElementById('slowlyincreaseTradingVolflucLL').value
    let flucPercentUL = document.getElementById('slowlyincreaseTradingVolflucUL').value
    let days = document.getElementById('slowlyincreaseTradingVoldays').value
    fetch('http://127.0.0.1:8081/stockinfo4j/search/slowlyincreasetradingvol?date=' + date +  '&flucPercentLL=' + flucPercentLL
        + '&flucPercentUL=' + flucPercentUL + '&days=' + days)
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            console.log(jsonObj);
            let err = jsonObj['errorMsg']
            if (err['code']==='0000') {
                document.getElementById('slowlyincreaseTradingVolstatus').innerText = 'OK'
                let slowlyincreaseTradingVoltbody = document.getElementById('slowlyincreaseTradingVoltbody');
                jsonObj['data'].forEach(function (data) {
                    slowlyincreaseTradingVoltbody.innerHTML += '<tr>' +
                        '<th scope="row">' + data['code'] + '</th>' +
                        '<td>' + data['name'] + '</td>' +
                        '<td>' + data['industry'] + '</td>' +
                        '<td>' + data['pastTradingVolAvg'] + '</td>' +
                        '<td>' + data['nowTradingVol'] + '</td>' +
                        '<td>' + data['flucPercent'] + '</td>' +
                        '</tr>';
                });
            } else {
                document.getElementById('slowlyincreaseTradingVolstatus').innerHTML = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('slowlyincreaseTradingVolstatus').innerHTML = error;
        });
}

function clearSlowlyincreaseTradingVol(){
    document.getElementById('slowlyincreaseTradingVolstatus').innerText = '';
    document.getElementById('slowlyincreaseTradingVoltbody').innerHTML = '';
}

function getFlucSearch() {
    clearFlucSearch()
    let date = document.getElementById('flucsearchdate').value
    let flucPercentLL = document.getElementById('flucsearchLL').value
    let flucPercentUL = document.getElementById('flucsearchUL').value
    fetch('http://127.0.0.1:8081/stockinfo4j/search/flucperanddate?date=' + date +  '&flucPercentLL=' + flucPercentLL
        + '&flucPercentUL=' + flucPercentUL)
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            console.log(jsonObj);
            let err = jsonObj['errorMsg']
            if (err['code']==='0000') {
                document.getElementById('flucsearchstatus').innerText = 'OK'
                let flucsearchtbody = document.getElementById('flucsearchtbody');
                jsonObj['data'].forEach(function (data) {
                    flucsearchtbody.innerHTML += '<tr>' +
                        '<th scope="row">' + data['code'] + '</th>' +
                        '<td>' + data['name'] + '</td>' +
                        '<td>' + data['industry'] + '</td>' +
                        '<td>' + data['closing'] + '</td>' +
                        '<td>' + data['fluc'] + '</td>' +
                        '<td>' + data['flucPer'] + '</td>' +
                        '</tr>';
                });
            } else {
                document.getElementById('flucsearchstatus').innerHTML = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('flucsearchstatus').innerHTML = error;
        });
}

function clearFlucSearch(){
    document.getElementById('flucsearchstatus').innerText = '';
    document.getElementById('flucsearchtbody').innerHTML = '';
}

function getOverboughtRanking(radioVal){
    clearOverboughtRanking()
    let date = document.getElementById('overboughtrankingdate').value
    fetch('http://127.0.0.1:8081/stockinfo4j/search/overbought?date=' + date +  '&overbought=' + radioVal)
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            console.log(jsonObj);
            let err = jsonObj['errorMsg']
            if (err['code']==='0000') {
                document.getElementById('overboughtrankingstatus').innerText = 'OK'
                let overboughtrankingtbody = document.getElementById('overboughtrankingtbody');
                jsonObj['data'].forEach(function (data) {
                    overboughtrankingtbody.innerHTML += '<tr>' +
                        '<th scope="row">' + data['code'] + '</th>' +
                        '<td>' + data['name'] + '</td>' +
                        '<td>' + data['industry'] + '</td>' +
                        '<td>' + data['overbought'] + '</td>' +
                        '<td>' + data['closing'] + '</td>' +
                        '<td>' + data['flucPer'] + '</td>' +
                        '<td>' + data['tradingAmount'] + '</td>' +
                        '</tr>';
                });
            } else {
                document.getElementById('overboughtrankingstatus').innerHTML = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('overboughtrankingstatus').innerHTML = error;
        });
}

function clearOverboughtRanking(){
    document.getElementById('overboughtrankingstatus').innerText = '';
    document.getElementById('overboughtrankingtbody').innerHTML = '';
}

function getBigBlackK() {
    clearBigBlackK()

    fetch('http://127.0.0.1:8081/stockinfo4j/search/bigblackk?date=' + document.getElementById('bigblackkdate').value)
        .then(res => {
            if (res.status === 200) {
                return res.json()
            } else {
                throw new Error(res.statusText)
            }
        })
        .then(jsonObj => {
            console.log(jsonObj);
            let err = jsonObj['errorMsg']
            if (err['code']==="0000") {
                document.getElementById('bigblackkstatus').innerHTML = 'OK';
                let bigBlackKtbody = document.getElementById('bigblackktbody');
                let bigBlackKTranstbody = document.getElementById('bigblackktranstbody');
                jsonObj['data'].forEach(function (data) {
                    bigBlackKtbody.innerHTML += '<tr>' +
                        '<th scope="row">' + data['code'] + '</th>' +
                        '<td>' + data['name'] + '</td>' +
                        '<td>' + data['industry'] + '</td>' +
                        '</tr>';

                    data['tranList'].forEach(function (datatrans) {
                        bigBlackKTranstbody.innerHTML += '<tr>' +
                            '<th scope="row">' + datatrans['code'] + '</th>' +
                            '<td>' + datatrans['name'] + '</td>' +
                            '<td>' + datatrans['tradingVol'] + '</td>' +
                            '<td>' + datatrans['opening'] + '</td>' +
                            '<td>' + datatrans['closing'] + '</td>' +
                            '<td>' + datatrans['flucPer'] + '</td>' +
                            '<td>' + datatrans['date'] + '</td>' +
                            '</tr>';
                    });
                });
            } else {
                document.getElementById('bigblackkstatus').innerHTML = err + '\n' + jsonObj['errorDetail'];
            }
        })
        .catch(error => {
            document.getElementById('bigblackkstatus').innerHTML = error;
        });
}

function clearBigBlackK(){
    document.getElementById('bigblackktbody').innerHTML=''
    document.getElementById('bigblackktranstbody').innerHTML=''
    document.getElementById('bigblackkstatus').innerHTML=''
}