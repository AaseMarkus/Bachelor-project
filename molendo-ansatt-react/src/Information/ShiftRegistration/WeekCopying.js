import React, { Component } from 'react'
import WeekCopyPoput from './../../assets/images/week-copy-popout.PNG'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class WeekCopying extends Component {
    render() {
        return (
            <Container>
                <Row>
                    <h3>Uker</h3>
                </Row>
                <Row>
                    <Image src={WeekCopyPoput} alt="Image of weekbox" width={200} />
                </Row>
                <Row>
                    Etter å ha trykket kopieringsknappen i ukesboksen åpnes en boks der man kan markere hvilke 
                    uker man har lyst til å kopiere vaktene til. Når man trykker på lagre blir alle vaktene i de 
                    markerte ukene erstattet med vaktene i den opprinnelige uken. Hvis man kopierer til nåværende uke
                    vil kun vaktene som ikke har passert bli kopiert inn.
                </Row>
            </Container>
        )
    }
}
