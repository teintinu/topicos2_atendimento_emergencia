var Mapa = React.createClass({
    getInitialState: function () {
        return {
            cidade: {},
            objetos: [],
            totais: {}
        };
    },
    atualiza: function () {
        var self = this;

        var r = new XMLHttpRequest();
        r.open("get", "mapa", true);
        r.onreadystatechange = function () {
            if (r.readyState != 4) return;
            setTimeout(self.atualiza, 33);

            if (r.status == 200) {
                var linhas = r.responseText.trim().split('\n');
                var cidade = linhas.shift().split(',');
                var objetos = linhas.map(function (l) {
                    var o = l.split(',');
                    return {
                        descricao: o[0],
                        tipo: o[1],
                        lat: o[2],
                        lng: o[3],
                        pos: o[4],
                        max: o[5]
                    };
                });
                var totais = {};
                objetos.forEach(function (o) {
                    if (totais[o.tipo])
                        totais[o.tipo] ++;
                    else
                        totais[o.tipo] = 1;
                });
                self.setState({
                    error: false,
                    cidade: {
                        nome: cidade[0],
                        tamanhoLat: cidade[1],
                        tamanhoLong: cidade[2]
                    },
                    objetos: objetos,
                    totais: totais
                });
            } else
                self.setState({
                    error: true
                });

        };
        r.send("");
    },
    componentDidMount: function () {
        setTimeout(this.atualiza, 33);
    },
    render: function () {
        var self = this;
        return React.createElement('h1', {}, React.createElement('a', {href: '#'}, 'Atendimento de emergencias em: '+this.state.cidade.nome),

            React.createElement('table', {
                className: 'objetos'
            }, [React.createElement('tr', {
                    className: 'titulo'
                },
                React.createElement('td', {
                    colSpan: 3
                }, "Objetos na tela por tipo")
                )].concat(Object.keys(self.state.totais).map(function (o) {
                return React.createElement('tr', {},
                    React.createElement('td', {}, o),
                    React.createElement('td', {}, self.state.totais[o])
                );
            })).concat(self.state.objetos.reduce(function (arr, o) {
                if (o.tipo == 'hospital') {
                    arr.push(React.createElement('tr', {},
                        React.createElement('td', {}, o.descricao),
                        React.createElement('td', {
                            colSpan: 2,
                            align: "right"
                        }, o.pos + ' de ' + o.max)
                    ));
                }
                return arr;
            }, [React.createElement('tr', {
                        className: 'titulo'
                    },
                    React.createElement('td', {}, "Hospital"),
                    React.createElement('td', {
                        colSpan: 2
                    }, "Ocupacao"))
                      ])).concat(self.state.objetos.reduce(function (arr, o) {
                if (o.tipo == 'ambulancia') {
                    arr.push(React.createElement('tr', {},
                        React.createElement('td', {}, React.createElement('a', {
                            href: '#' + o.descricao
                        }, o.descricao)),
                        React.createElement('td', {}, o.lat + ',' + o.lng),
                        React.createElement('td', {}, ['Livre',
                                                       'Indo buscar paciente: ' + o.max,
                                                       'Transportando paciente: ' + o.max][o.pos])
                    ));
                }
                return arr;
            }, [React.createElement('tr', {
                    className: 'titulo'
                },
                React.createElement('td', {}, "Ambulancias"),
                React.createElement('td', {}, "Posicao"),
                React.createElement('td', {}, "Situacao")
                      )]))),

            React.createElement('div', {
                className: 'mapa',
                onClick: this.criarEmergencia,
                style: {
                    width: self.state.cidade.tamanhoLat,
                    height: self.state.cidade.tamanhoLong
                }
            }, (self.state.error ? '  (ERRO DE COMUNICACAO COM O SERVIDOR)' :
                self.state.objetos.map(function (o) {
                    return React.createElement('div', {
                        id: o.descricao,
                        className: o.tipo == 'ambulancia' ? o.tipo + " " + o.descricao : o.tipo,
                        ocupacao: o.pos / o.max,
                        key: o.tipo + "_" + o.descricao,
                        title: o.descricao,
                        style: {
                            left: o.lat,
                            top: o.lng
                        }
                    });
                })))
        );
    },
    criarEmergencia: function (event) {
        var totalOffsetX = 0;
        var totalOffsetY = 0;
        var canvasX = 0;
        var canvasY = 0;
        var currentElement = event.target;
        if (!event.target.classList.contains('mapa'))
            return;

        do {
            totalOffsetX += currentElement.offsetLeft - currentElement.scrollLeft;
            totalOffsetY += currentElement.offsetTop - currentElement.scrollTop;
        }
        while (currentElement = currentElement.offsetParent)

        canvasX = event.pageX - totalOffsetX;
        canvasY = event.pageY - totalOffsetY;

        var r = new XMLHttpRequest();
        r.open("get", "criaemergencia/" + canvasX + "," + canvasY, true);
        r.onreadystatechange = function () {

        };
        r.send("");
    }

});

React.render(React.createElement(Mapa), document.getElementById('mapa'));
