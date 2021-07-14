import React, { Component } from 'react'
import DayButtons from './DayButtons'
import DayBoxImage from './../../assets/images/registration-day-box.PNG'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class DayBox extends Component {
    render() {
        return (
            <div>
                <Row>
                    <h2>Dagsregistrering</h2>
                </Row>
                <Row>
                    <Image src={DayBoxImage} alt="Image of weekbox" width={400}/>
                </Row>
                <Row>
                    TODO: beskriv dagsboks (dag er starttid-sluttid ikke nødvendigvis på gitt dato)
                </Row>

                <br />
                <DayButtons />
            </div>
        )
    }
}
