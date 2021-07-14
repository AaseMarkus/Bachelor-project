import React, { Component } from 'react'
import WeekButtons from './WeekButtons'
import WeekImage from './../../assets/images/registration-week-box.PNG'
import { Container, Row, Col, Image } from 'react-bootstrap'

export default class WeekBox extends Component {
    render() {
        return (
            <div>
                <Row>
                    <h2>Ukesregistrering</h2>
                </Row>

                <Row>
                    <Image src={WeekImage} alt="Image of weekbox" fluid/>
                </Row>
                
                <Row>
                    Vaktregistrering er delt inn i forskjellige uker, som videre er delt inn i
                    forskjellige dager. Øverst i ukeboksene, og i dagsboksene finner man 
                    knapper som brukes for å modifisere vaktene.
                </Row>

                <br />
                <WeekButtons />
            </div>
        )
    }
}
